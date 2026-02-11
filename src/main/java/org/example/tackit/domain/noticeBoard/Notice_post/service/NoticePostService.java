package org.example.tackit.domain.noticeBoard.Notice_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.PageResponseDTO;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.noticeBoard.Notice_post.dto.request.NoticePostReqDto;
import org.example.tackit.domain.noticeBoard.Notice_post.dto.request.UpdateNoticeReqDto;
import org.example.tackit.domain.noticeBoard.Notice_post.dto.response.NoticePostRespDto;
import org.example.tackit.domain.noticeBoard.Notice_post.dto.response.NoticeScrapRespDto;
import org.example.tackit.domain.noticeBoard.Notice_post.repository.NoticePostImageRepository;
import org.example.tackit.domain.noticeBoard.Notice_post.repository.NoticePostRepository;
import org.example.tackit.domain.noticeBoard.Notice_post.repository.NoticeScrapRepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.notification.service.NotificationService;
import org.example.tackit.global.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.example.tackit.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticePostService {
    private final NoticePostRepository noticePostRepository;
    private final NoticeScrapRepository noticeScrapRepository;
    private final S3UploadService s3UploadService;
    private final NoticePostImageRepository noticePostImageRepository;
    private final NotificationService notificationService;
    private final MemberOrgRepository memberOrgRepository;

    // [ 게시글 전체 조회 ]
    @Transactional
    public PageResponseDTO<NoticePostRespDto> findAll(Long orgId, Pageable pageable ) {

        Page<NoticePost> page = noticePostRepository.findByWriterId(orgId, pageable);
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
                            .commentEnabled(post.isCommentEnabled())
                            .build();
        });
    }

    // [ 게시글 상세 조회 ]
    @Transactional
    public NoticePostRespDto getPostById(Long id, Long orgId, Long memberId) {
        NoticePost post = noticePostRepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        // 해당 게시글이 현재 접속한 소속의 글인지 검증
        if (!post.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_ORGANIZATION);
        }
        post.increaseViewCount();

        String imageUrl = post.getImages().isEmpty() ? null
                : post.getImages().get(0).getImageUrl();

        String profileImageUrl = post.getWriter().getProfileImageUrl();

        // 스크랩 여부 조회
        boolean isScrap = noticeScrapRepository.existsByNoticePostIdAndMemberId(id, memberId);

        return NoticePostRespDto.builder()
                .id(post.getId())
                .writer(post.getWriter().getNickname())
                .profileImageUrl(post.getWriter().getProfileImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(imageUrl)
                .createdAt(post.getCreatedAt())
                .isScrap(isScrap)
                .commentEnabled(post.isCommentEnabled())
                .build();
    }

    // [ 게시글 작성 ]
    @Transactional
    public NoticePostRespDto createPost(NoticePostReqDto dto, MultipartFile image, String email, Long orgId) throws IOException {

        // 현재 접속한 프로필 조회
        MemberOrg writerProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

        // 권한 확인 = 동아리 운영진만 작성 가능
        if (writerProfile.getMemberRole() != MemberRole.EXECUTIVE) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_NOTICE);
        }

        // 2. 게시글 생성
        NoticePost post = NoticePost.builder()
                        .writer(writerProfile)
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .createdAt(LocalDateTime.now())
                        .type(Post.Notice)
                        .commentEnabled(dto.isCommentEnabled())
                        .build();

        noticePostRepository.save(post);

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
    public NoticePostRespDto update(Long id, UpdateNoticeReqDto req, MultipartFile image, String email, Long orgId) throws IOException {

        MemberOrg memberProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        NoticePost post = noticePostRepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        // 작성자 본인(프로필 ID 기준)인지 확인
        if (!post.getWriter().getId().equals(memberProfile.getId())) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_EDIT);
        }

        post.update(req.getTitle(), req.getContent(), req.isCommentEnabled());

        String imageUrl = null;

        // 1. "이미지 제거" 요청
        if (req.isRemoveImage()) {
            deleteExistingImages(post);
            imageUrl = null;
        }

        // 2. 새 이미지 업로드
        else if (image != null && !image.isEmpty()) {
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
                    .stream()
                    .findFirst()
                    .map(NoticePostImage::getImageUrl)
                    .orElse(null);
        }

        return NoticePostRespDto.from(post, imageUrl);
    }

    // [ 게시글 삭제 ] : 작성자, 관리자만
    @Transactional
    public void delete(Long id, String email, Long orgId) {
        MemberOrg memberProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        NoticePost post = noticePostRepository.findById(id)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        boolean isWriter = post.getWriter().getId().equals(memberProfile.getId());
        boolean isAdmin = (memberProfile.getMemberRole() == MemberRole.ADMIN);

        if (!isAdmin && !isWriter) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_DELETE);
        }

        noticePostRepository.delete(post);
    }


    // [ 게시글 스크랩 ]
    @Transactional
    public NoticeScrapRespDto toggleScrap(Long postId, String email, Long orgId) {
        MemberOrg memberProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        NoticePost post = noticePostRepository.findById(postId)
                .orElseThrow( () -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND) );

        // 같은 소속인지 체크
        if (!post.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedCustomException(ErrorCode.ACCESS_DENIED_ORGANIZATION);
        }

        Optional<NoticeScrap> existing = noticeScrapRepository.findByMemberAndNoticePost(memberProfile.getMember(), post);

        if (existing.isPresent()) {
            noticeScrapRepository.delete(existing.get());
            post.decreaseScrapCount();
            return new NoticeScrapRespDto(false, null);
        }

        NoticeScrap scrap = NoticeScrap.builder()
                .member(memberProfile.getMember())
                .noticePost(post)
                .savedAt(LocalDateTime.now())
                .build();

        noticeScrapRepository.save(scrap);
        post.increaseScrapCount();

        // 1. 알림 전송
        if(!post.getWriter().getId().equals(memberProfile.getId())){

            String message = memberProfile.getNickname() + "님이 글을 스크랩하였습니다.";

            // 2. 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(post.getWriter().getMember())
                    .memberOrgId(post.getWriter().getId())
                    .type(NotificationType.SCRAP)
                    .message(message)
                    .fromMemberOrgId(memberProfile.getId())
                    .relatedUrl("/api/notice-posts/" + post.getId())
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
