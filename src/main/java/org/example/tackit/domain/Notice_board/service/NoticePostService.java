package org.example.tackit.domain.Notice_board.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.PageResponseDTO;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.Notice_board.dto.request.NoticePostReqDto;
import org.example.tackit.domain.Notice_board.dto.request.UpdateNoticeReqDto;
import org.example.tackit.domain.Notice_board.dto.response.NoticePostRespDto;
import org.example.tackit.domain.Notice_board.dto.response.NoticeScrapRespDto;
import org.example.tackit.domain.Notice_board.repository.NoticePostImageRepository;
import org.example.tackit.domain.Notice_board.repository.NoticePostRepository;
import org.example.tackit.domain.Notice_board.repository.NoticeScrapRepository;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.example.tackit.global.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.example.tackit.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticePostService {
    private final NoticePostRepository noticePostRepository;
    private final MemberRepository memberRepository;
    private final NoticeScrapRepository noticeScrapRepository;
    private final S3UploadService s3UploadService;
    private final NoticePostImageRepository noticePostImageRepository;
    private final NotificationService notificationService;

    // [ 게시글 전체 조회 ]
    @Transactional
    public PageResponseDTO<NoticePostRespDto> findAll(String org, Pageable pageable ) {

        Page<NoticePost> page = noticePostRepository.findByOrganization(org, pageable);

        return PageResponseDTO.from(page, post -> {
                    String imageUrl = post.getImages().isEmpty() ? null
                            : post.getImages().get(0).getImageUrl();

                    return NoticePostRespDto.builder()
                            .id(post.getId())
                            .writer(post.getWriter().getNickname())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .imageUrl(imageUrl)
                            .createdAt(post.getCreatedAt())
                            .build();
        });
    }

    // [ 게시글 상세 조회 ]
    @Transactional
    public NoticePostRespDto getPostById(Long id, String org, Long memberId) {
        NoticePost post = noticePostRepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        if (!post.getOrganization().equals(org)) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_ORGANIZATION);
        }

        /*
        if (!post.getStatus().equals(Status.ACTIVE)) {
            throw new PostInactiveException(ErrorCode.POST_IS_INACTIVE);
        }
         */

        post.increaseViewCount();

        String imageUrl = post.getImages().isEmpty() ? null
                : post.getImages().get(0).getImageUrl();

        String profileImageUrl = post.getWriter().getProfileImageUrl();

        // 스크랩 여부 조회
        boolean isScrap = noticeScrapRepository.existsByNoticePostIdAndMemberId(id, memberId);

        return NoticePostRespDto.builder()
                .id(post.getId())
                .writer(post.getWriter().getNickname())
                .profileImageUrl(profileImageUrl)
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(imageUrl)
                .createdAt(post.getCreatedAt())
                .isScrap(isScrap)
                .build();
    }

    // [ 게시글 작성 ]
    @Transactional
    public NoticePostRespDto createPost(NoticePostReqDto dto, MultipartFile image, String email, String org) throws IOException {

        // 1. 유저 조회
        Member member = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        // 2. 게시글 생성
        NoticePost post = NoticePost.builder()
                        .writer(member)
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .createdAt(LocalDateTime.now())
                        .type(Post.Notice)
                        .organization(org)
                        .build();

        noticePostRepository.save(post);

        // 3. 이미지 업로드 → PostImage 저장
        /*
        String imageUrl = null;
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            imageUrl = s3UploadService.saveFile(dto.getImage());

            NoticePostImage image = NoticePostImage.builder()
                    .imageUrl(imageUrl)
                    .noticePost(post)
                    .build();

            noticePostImageRepository.save(image); // 따로 JPARepository 필요
        }
         */

        // 3. 이미지 업로드 -> 인자로 받은 image 직접 사용
        String imageUrl = null;
        if(image != null && !image.isEmpty()) {
            imageUrl = s3UploadService.saveFile(image);

            NoticePostImage postImage = NoticePostImage.builder()
                    .imageUrl(imageUrl)
                    .noticePost(post)
                    .build();

            noticePostImageRepository.save(postImage);
        }

        return NoticePostRespDto.from(post, imageUrl);

    }

    // [ 게시글 수정 ] : 작성자만
    @Transactional
    public NoticePostRespDto update(Long id, UpdateNoticeReqDto req, MultipartFile image, String email, String org) throws IOException {
        Member member = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        NoticePost post = noticePostRepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        boolean isWriter = post.getWriter().getId().equals(member.getId());

        if (!isWriter) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_EDIT);
        }

        post.update(req.getTitle(), req.getContent());

        String imageUrl = null;

        // 1. "이미지 제거" 요청
        if (req.isRemoveImage()) {
            deleteExistingImages(post);
        }

        // 2. 새 이미지 업로드
        else if (req.getImage() != null && !req.getImage().isEmpty()) {
            // 기존 이미지 제거
            deleteExistingImages(post);
            imageUrl = s3UploadService.saveFile(image);

            NoticePostImage newImage = NoticePostImage.builder()
                    .imageUrl(imageUrl)
                    .noticePost(post)
                    .build();

            noticePostImageRepository.save(newImage);
        }

        // 3. 아무 요청 없으면 기존 이미지 유지
        else {
            imageUrl = noticePostImageRepository.findByNoticePostId(post.getId())
                    .stream().findFirst().map(NoticePostImage::getImageUrl).orElse(null);
        }

        return NoticePostRespDto.from(post, imageUrl);
    }

    // [ 게시글 삭제 ] : 작성자, 관리자만
    @Transactional
    public void delete(Long id, String email, String org) {
        Member member = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        NoticePost post = noticePostRepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        boolean isWriter = post.getWriter().getId().equals(member.getId());
        boolean isAdmin = (member.getMemberRole() == MemberRole.ADMIN)
                && (member.getMemberType() == MemberType.ADMIN);

        if (!isAdmin && !isWriter) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_DELETE);
        }

        noticePostRepository.delete(post);
    }


    // [ 게시글 스크랩 ]
    @Transactional
    public NoticeScrapRespDto toggleScrap(Long postId, String email, String org) {
        Member member = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        NoticePost post = noticePostRepository.findById(postId)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        if (!post.getOrganization().equals(org)) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_ORGANIZATION);
        }

        Optional<NoticeScrap> existing = noticeScrapRepository.findByMemberAndNoticePost(member, post);

        if (existing.isPresent()) {
            noticeScrapRepository.delete(existing.get());
            post.decreaseScrapCount();
            return new NoticeScrapRespDto(false, null);
        }

        NoticeScrap scrap = NoticeScrap.builder()
                .member(member)
                .noticePost(post)
                .savedAt(LocalDateTime.now())
                .build();

        noticeScrapRepository.save(scrap);
        post.increaseScrapCount();

        // 1. 알림 전송
        if(!post.getWriter().getId().equals(member.getId())){
            Member postWriter = post.getWriter();
            String message = member.getNickname() + "님이 글을 스크랩하였습니다.";
            String url = "/api/notice-posts/" + post.getId();

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
        return new NoticeScrapRespDto(true, scrap.getSavedAt());
    }

    public void deleteExistingImages(NoticePost post) {
        noticePostImageRepository.findByNoticePostId(post.getId())
                .forEach(oldImage -> {
                    s3UploadService.deleteImage(oldImage.getImageUrl());
                    noticePostImageRepository.delete(oldImage);
                });
    }

}
