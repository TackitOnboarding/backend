package org.example.tackit.domain.Tip_board.Tip_post.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.Tip_board.Tip_post.dto.response.TipPopularPostRespDto;
import org.example.tackit.domain.Tip_board.Tip_post.dto.response.TipPostRespDto;
import org.example.tackit.domain.Tip_board.Tip_post.dto.response.TipScrapRespDto;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipMemberJPARepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostReportRepository;
import org.example.tackit.domain.Tip_board.Tip_tag.repository.TipPostTagMapRepository;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.Tip_board.Tip_post.dto.request.TipPostReqDto;
import org.example.tackit.domain.Tip_board.Tip_post.dto.request.TipPostUpdateDto;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostJPARepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipScrapRepository;
import org.example.tackit.domain.notification.service.NotificationService;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipPostService {
    private final TipPostJPARepository tipPostJPARepository;
    private final TipMemberJPARepository tipMemberJPARepository;
    private final TipScrapRepository tipScrapRepository;
    private final TipPostReportRepository tipPostReportRepository;
    private final TipPostTagMapRepository tipPostTagMapRepository;
    private final TipTagService tagService;
    private final S3UploadService s3UploadService;
    private final NotificationService notificationService;

    public PageResponseDTO<TipPostRespDto> getActivePostsByOrganization(String org, Pageable pageable) {
        Page<TipPost> page = tipPostJPARepository.findByOrganizationAndStatus(org, Status.ACTIVE, pageable);

        return PageResponseDTO.from(page, post -> {
                    List<String> tags = tipPostTagMapRepository.findByTipPost(post).stream()
                            .map(mapping -> mapping.getTag().getTagName())
                            .toList();

                    return TipPostRespDto.builder()
                            .id(post.getId())
                            .writer(post.getWriter().getNickname())
                            .profileImageUrl(post.getWriter().getProfileImageUrl())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .createdAt(post.getCreatedAt())
                            .tags(tags)
                            .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
                            .build();
        });
    }

    // [ 게시글 상세 조회 ]
    @Transactional
    public TipPostRespDto getPostById(Long id, String org, Long memberId) {
        TipPost tipPost = tipPostJPARepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!tipPost.getOrganization().equals(org)) {
            throw new AccessDeniedException("해당 조직의 게시글이 아닙니다.");
        }

        if (!tipPost.getStatus().equals(Status.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비활성화된 게시글입니다.");
        }

        tipPost.increaseViewCount();

        List<String> tagNames = tagService.getTagNamesByPost(tipPost);

        // 스크랩 여부 조회
        boolean isScrap = tipScrapRepository.existsByTipPostIdAndMemberId(id, memberId);

        return TipPostRespDto.builder()
                .id(tipPost.getId())
                .writer(tipPost.getWriter().getNickname())
                .profileImageUrl(tipPost.getWriter().getProfileImageUrl())
                .title(tipPost.getTitle())
                .content(tipPost.getContent())
                .tags(tagNames)
                .imageUrl(tipPost.getImages().isEmpty() ? null : tipPost.getImages().get(0).getImageUrl())
                .createdAt(tipPost.getCreatedAt())
                .isScrap(isScrap)
                .build();
    }

    // [ 게시글 작성 ] : 선임자만 가능
    @Transactional
    public TipPostRespDto createPost(TipPostReqDto dto, String email, String org, MultipartFile image) throws IOException {
        // 1. 유저 조회
        Member member = tipMemberJPARepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("작성자가 DB에 존재하지 않습니다."));

        if (member.getRole() != Role.SENIOR) {
            throw new AccessDeniedException("SENIOR만 게시글을 작성할 수 있습니다.");
        }

        // 2. 게시글 생성
        TipPost post = TipPost.builder()
                .writer(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .type(Post.Tip)
                .status(Status.ACTIVE)
                .reportCount(0)
                .organization(org)
                .isAnonymous(dto.isAnonymous())
                .build();

        // 3. 이미지 업로드 & 연관관계 매핑 (단일 파일만)
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3UploadService.saveFile(image);
            TipPostImage imageEntity = TipPostImage.builder()
                    .imageUrl(imageUrl)
                    .build();
            post.addImage(imageEntity); // 기존 이미지 clear 후 하나만 저장
        }

        tipPostJPARepository.save(post);

        List<String> tagNames = tagService.assignTagsToPost(post, dto.getTagIds());

        boolean anonymous = post.isAnonymous();

        // 응답 DTO 구성 (imageUrl 하나만)
        return TipPostRespDto.builder()
                .id(post.getId())
                .writer(anonymous ? "익명" : member.getNickname())
                .profileImageUrl(anonymous ? null : member.getProfileImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .tags(tagNames)
                .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
                .isAnonymous(anonymous)
                .isScrap(false)
                .build();
    }



    // [ 게시글 수정 ] : 작성자만
    @Transactional
    public TipPostRespDto update(Long id, TipPostUpdateDto dto, String email, String org, MultipartFile image) throws IOException {
        Member member = tipMemberJPARepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TipPost post = tipPostJPARepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getWriter().getId().equals(member.getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        // 기본 정보 수정
        post.update(dto.getTitle(), dto.getContent());

        // 이미지 수정 로직
        String currentImageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();

        // 1) 삭제 요청
        if (Boolean.TRUE.equals(dto.getRemoveImage())) {
            if (currentImageUrl != null) {
                s3UploadService.deleteImage(currentImageUrl);
            }
            post.clearImages();
        }

        // 2) 새 이미지 업로드 (교체 or 추가)
        if (image != null && !image.isEmpty()) {
            if (currentImageUrl != null) {
                s3UploadService.deleteImage(currentImageUrl);
                post.clearImages();
            }
            String newImageUrl = s3UploadService.saveFile(image);
            TipPostImage newImage = TipPostImage.builder()
                    .imageUrl(newImageUrl)
                    .build();
            post.addImage(newImage);
        }

        // 태그 다시 매핑
        tagService.deleteTagsByPost(post);
        List<String> tagNames = tagService.assignTagsToPost(post, dto.getTagIds());

        return TipPostRespDto.builder()
                .id(post.getId())
                .writer(post.getWriter().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .tags(tagNames)
                .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
                .build();
    }


    // [ 게시글 삭제 ]
    @Transactional
    public void deletePost(Long id, CustomUserDetails user) {
        TipPost post = tipPostJPARepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 권한 체크 : 요청 유저가 작성자인지
        if(!post.getWriter().getId().equals(user.getId())) {
            throw new AccessDeniedException("해당 게시글을 수정할 권한이 없습니다.");
        }
        post.delete();
    }

    // [ 게시글 스크랩 ]
    @Transactional
    public TipScrapRespDto toggleScrap(Long id, String email, String memberOrg) {
        Member member = tipMemberJPARepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TipPost post = tipPostJPARepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if(!post.getWriter().getOrganization().equals(memberOrg)) {
            throw new AccessDeniedException("해당 조직 게시글이 아닙니다.");
        }

        Optional<TipScrap> exisiting = tipScrapRepository.findByMemberAndTipPost(member, post);

        if (exisiting.isPresent()) {
            tipScrapRepository.delete(exisiting.get());
            post.decreaseScrapCount();
            return new TipScrapRespDto(false, null);
        }
        TipScrap scrap = TipScrap.builder()
                .member(member)
                .tipPost(post)
                .savedAt(LocalDateTime.now())
                .build();

        tipScrapRepository.save(scrap);
        post.increaseScrapCount();

        // 알림 전송
        if(!post.getWriter().getId().equals(member.getId())) {
            Member postWriter = post.getWriter();
            String message = member.getNickname() + "님이 글을 스크랩하였습니다.";
            String url = "/api/tip-posts/" + post.getId();

            // 알림 엔티티 생성
            // 2. 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(postWriter)
                    .type(NotificationType.SCRAP)
                    .message(message)
                    .relatedUrl(url)
                    .fromMemberId(member.getId())
                    .build();

            //3. 알림 저장 및 전송을 위해 NotificationService 호출
            notificationService.send(notification);
        }
        return new TipScrapRespDto(true, scrap.getSavedAt());
    }


    // [ 게시글 신고 ]
    @Transactional
    public String report(Long postId, Long userId) {
        TipPost post = tipPostJPARepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = tipMemberJPARepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.") );

        boolean alreadyReported = tipPostReportRepository.existsByMemberAndTipPost(member, post);

        if (alreadyReported) {
            return "이미 신고한 게시글입니다.";
        }
        tipPostReportRepository.save(
                TipReport.builder()
                        .member(member)
                        .tipPost(post)
                        .build()
        );
        // 신고 횟수 증가
        post.increaseReportCount();
        return "게시글을 신고하였습니다.";
    }

    // 인기 3개
    @Transactional
    public List<TipPopularPostRespDto> getPopularPosts(String organization) {
        return tipPostJPARepository.findTop3ByStatusOrderByViewCountDescScrapCountDesc(Status.ACTIVE)
                .stream()
                .filter(post -> post.getWriter().getOrganization().equals(organization))
                .sorted(Comparator
                        .comparing(
                                (TipPost post) -> post.getViewCount() == null ? 0L : post.getViewCount(),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                post -> post.getScrapCount() == null ? 0L : post.getScrapCount(),
                                Comparator.reverseOrder()
                        )
                )
                .limit(3)
                .map(TipPopularPostRespDto::from)
                .toList();
    }


}
