package org.example.tackit.domain.Free_board.Free_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.Free_board.Free_post.dto.request.FreePostReqDto;
import org.example.tackit.domain.Free_board.Free_post.dto.request.UpdateFreeReqDto;
import org.example.tackit.domain.Free_board.Free_post.dto.response.FreePopularPostRespDto;
import org.example.tackit.domain.Free_board.Free_post.dto.response.FreePostRespDto;
import org.example.tackit.domain.Free_board.Free_post.dto.response.FreeScrapResponseDto;
import org.example.tackit.domain.Free_board.Free_post.repository.*;
import org.example.tackit.domain.Free_board.Free_tag.repository.FreePostTagMapRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.example.tackit.common.dto.PageResponseDTO;
import org.example.tackit.global.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.example.tackit.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FreePostService {
    private final FreePostJPARepository freePostJPARepository;
    private final FreeMemberJPARepository freeMemberJPARepository;
    private final FreePostTagService tagService;
    private final FreeScrapJPARepository freeScrapJPARepository;
    private final FreePostTagMapRepository freePostTagMapRepository;
    private final FreePostReportRepository freePostReportRepository;
    private final S3UploadService s3UploadService;
    private final FreePostImageRepository freePostImageRepository;
    private final NotificationService notificationService;

    // [ 게시글 전체 조회 ]
    @Transactional
    public PageResponseDTO<FreePostRespDto> findAll(String org, Pageable pageable ) {
        Page<FreePost> page = freePostJPARepository.findByOrganizationAndStatus(org, Status.ACTIVE, pageable);

        return PageResponseDTO.from(page, post -> {
            List<String> tags = freePostTagMapRepository.findByFreePost(post).stream()
                    .map(mapping -> mapping.getTag().getTagName())
                    .toList();

            String imageUrl = post.getImages().isEmpty() ? null
                    : post.getImages().get(0).getImageUrl();

            String profileImageUrl = post.getWriter().getProfileImageUrl();

            return FreePostRespDto.builder()
                    .id(post.getId())
                    .writer(post.isAnonymous() ? "익명" : post.getWriter().getNickname())
                    .profileImageUrl(post.isAnonymous() ? null : imageUrl)
                    .title(post.getTitle())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .tags(tags)
                    .imageUrl(imageUrl)
                    .isAnonymous(post.isAnonymous())
                    .build();
        });
    }

    // [ 게시글 상세 조회 ]
    @Transactional
    public FreePostRespDto getPostById(Long id, String org, Long memberId) {
        FreePost post = freePostJPARepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        if (!post.getOrganization().equals(org)) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_ORGANIZATION);
        }

        if (!post.getStatus().equals(Status.ACTIVE)) {
            throw new PostInactiveException(ErrorCode.POST_IS_INACTIVE);
        }

        post.increaseViewCount();

        List<String> tagNames = tagService.getTagNamesByPost(post);

        String imageUrl = post.getImages().isEmpty() ? null
                : post.getImages().get(0).getImageUrl();

        String profileImageUrl = post.getWriter().getProfileImageUrl();

        // 스크랩 여부 조회
        boolean isScrap = freeScrapJPARepository.existsByFreePostIdAndMemberId(id, memberId);

        return FreePostRespDto.builder()
                .id(post.getId())
                .writer(post.isAnonymous() ? "익명" : post.getWriter().getNickname())
                .profileImageUrl(post.isAnonymous() ? null : profileImageUrl)
                .title(post.getTitle())
                .content(post.getContent())
                .tags(tagNames)
                .imageUrl(imageUrl)
                .createdAt(post.getCreatedAt())
                .isScrap(isScrap)
                .isAnonymous(post.isAnonymous())
                .build();
    }

    // [ 게시글 작성 ]
    @Transactional
    public FreePostRespDto createPost(FreePostReqDto dto, String email, String org) throws IOException {

        // 1. 유저 조회
        Member member = freeMemberJPARepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        // 2. 게시글 생성
        FreePost post = FreePost.builder()
                        .writer(member)
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .isAnonymous(dto.isAnonymous())
                        .createdAt(LocalDateTime.now())
                        .type(Post.Free)
                        .status(Status.ACTIVE)
                        .reportCount(0)
                        .organization(org)
                        .build();

        freePostJPARepository.save(post);

        // 3. 이미지 업로드 → PostImage 저장
        String imageUrl = null;
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            imageUrl = s3UploadService.saveFile(dto.getImage());

            FreePostImage image = FreePostImage.builder()
                    .imageUrl(imageUrl)
                    .freePost(post)
                    .build();

            freePostImageRepository.save(image); // 따로 JPARepository 필요
        }

        List<String> tagNames = tagService.assignTagsToPost(post, dto.getTagIds());

        return FreePostRespDto.builder()
                .id(post.getId())
                .writer(post.isAnonymous() ? "익명" : member.getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .tags(tagNames)
                .imageUrl(imageUrl)
                .isAnonymous(post.isAnonymous())
                .build();

    }

    // [ 게시글 수정 ] : 작성자만
    @Transactional
    public FreePostRespDto update(Long id, UpdateFreeReqDto req, String email, String org) throws IOException {
        Member member = freeMemberJPARepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        FreePost post = freePostJPARepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        boolean isWriter = post.getWriter().getId().equals(member.getId());

        if (!isWriter) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_EDIT);
        }

        post.update(req.getTitle(), req.getContent());

        tagService.deleteTagsByPost(post);
        List<String> tagNames = tagService.assignTagsToPost(post, req.getTagIds());

        String imageUrl = null;
        // 1. "이미지 제거" 요청
        if (req.isRemoveImage()) {
            freePostImageRepository.findByFreePostId(post.getId())
                    .forEach(oldImage -> {
                        s3UploadService.deleteImage(oldImage.getImageUrl()); // S3 삭제
                        freePostImageRepository.delete(oldImage);           // DB 삭제
                    });
        }

        // 2. 새 이미지 업로드
        else if (req.getImage() != null && !req.getImage().isEmpty()) {
            // 기존 이미지 제거
            freePostImageRepository.findByFreePostId(post.getId())
                    .forEach(oldImage -> {
                        s3UploadService.deleteImage(oldImage.getImageUrl());
                        freePostImageRepository.delete(oldImage);
                    });

            // 새 이미지 저장
            imageUrl = s3UploadService.saveFile(req.getImage());
            FreePostImage newImage = FreePostImage.builder()
                    .imageUrl(imageUrl)
                    .freePost(post)
                    .build();

            freePostImageRepository.save(newImage);
        }

        // 3. 아무 요청 없으면 기존 이미지 유지
        else {
            List<FreePostImage> images = freePostImageRepository.findByFreePostId(post.getId());
            if (!images.isEmpty()) {
                imageUrl = images.get(0).getImageUrl();
            }
        }

        return FreePostRespDto.builder()
                .id(post.getId())
                .writer(post.isAnonymous() ? "익명" : member.getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .tags(tagNames)
                //.imageUrl(imageUrl)
                .imageUrl(post.isAnonymous() ? null : imageUrl)
                .isAnonymous(post.isAnonymous())
                .build();
    }

    // [ 게시글 삭제 ] : 작성자, 관리자만
    @Transactional
    public void delete(Long id, String email, String org) {
        Member member = freeMemberJPARepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        FreePost post = freePostJPARepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        boolean isWriter = post.getWriter().getId().equals(member.getId());
        boolean isAdmin = member.getRole() == Role.ADMIN;

        if (!isAdmin && !isWriter) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_DELETE);
        }

        post.delete(); // Soft Deleted
    }

    // [ 게시글 신고 ]
    @Transactional
    public String report(Long postId, Long userId) {
        Member member = freeMemberJPARepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        FreePost post = freePostJPARepository.findById(postId)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        boolean alreadyReported = freePostReportRepository.existsByMemberAndFreePost(member, post);

        if (alreadyReported) {
            return "이미 신고한 게시글입니다.";
        }
        freePostReportRepository.save(
                FreeReport.builder()
                        .member(member)
                        .freePost(post)
                        .build()
        );
        // 신고 횟수 증가
        post.increaseReportCount();
        return "게시글을 신고하였습니다.";
    }


    // [ 게시글 스크랩 ]
    @Transactional
    public FreeScrapResponseDto toggleScrap(Long postId, String email, String org) {
        Member member = freeMemberJPARepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        FreePost post = freePostJPARepository.findById(postId)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        if (!post.getOrganization().equals(org)) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_ORGANIZATION);
        }

        Optional<FreeScrap> existing = freeScrapJPARepository.findByMemberAndFreePost(member, post);

        if (existing.isPresent()) {
            freeScrapJPARepository.delete(existing.get());
            post.decreaseScrapCount();
            return new FreeScrapResponseDto(false, null);
        }

        FreeScrap scrap = FreeScrap.builder()
                .member(member)
                .freePost(post)
                .savedAt(LocalDateTime.now())
                .build();

        freeScrapJPARepository.save(scrap);
        post.increaseScrapCount();

        // 1. 알림 전송
        if(!post.getWriter().getId().equals(member.getId())){
            Member postWriter = post.getWriter();
            String message = member.getNickname() + "님이 글을 스크랩하였습니다.";
            String url = "/api/free-posts/" + post.getId();

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
        return new FreeScrapResponseDto(true, scrap.getSavedAt());
    }

    // 인기 3개
    @Transactional(readOnly = true)
    public List<FreePopularPostRespDto> getPopularPosts(String organization) {
        return freePostJPARepository.findTop3ByStatusOrderByViewCountDescScrapCountDesc(Status.ACTIVE)
                .stream()
                .filter(post -> post.getWriter().getOrganization().equals(organization))
                .sorted(Comparator
                        .comparing(
                                (FreePost post) -> post.getViewCount() == null ? 0L : post.getViewCount(),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                post -> post.getScrapCount() == null ? 0L : post.getScrapCount(),
                                Comparator.reverseOrder()
                        )
                )
                .limit(3)
                .map(FreePopularPostRespDto::from)
                .toList();
    }

}
