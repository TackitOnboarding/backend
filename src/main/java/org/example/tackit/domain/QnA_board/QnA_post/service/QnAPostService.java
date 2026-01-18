package org.example.tackit.domain.QnA_board.QnA_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.QnA_board.QnA_post.dto.request.QnAPostReqDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.request.UpdateQnARequestDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.response.QnAPopularPostRespDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.response.QnAPostRespDto;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAMemberRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostReportRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAScrapRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QnAPostService {

    private final QnAPostRepository qnAPostRepository;
    private final QnAMemberRepository qnAMemberRepository;
    private final QnAPostTagService tagService;
    private final QnAPostReportRepository qnAPostReportRepository;
    private final S3UploadService s3UploadService;
    private final QnAScrapRepository qnAScrapRepository;

    // 게시글 작성 (NEWBIE만 가능)
    @Transactional
    public QnAPostRespDto createPost(QnAPostReqDto dto, String email, String org) throws IOException {
        Member member = qnAMemberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (member.getRole() != Role.NEWBIE) {
            throw new AccessDeniedException("NEWBIE만 질문을 작성할 수 있습니다.");
        }

        QnAPost post = QnAPost.builder()
                .writer(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .type(Post.QnA)
                .organization(org)
                .status(Status.ACTIVE)
                .reportCount(0)
                .isAnonymous(dto.isAnonymous())
                .build();

        // 이미지가 있으면 추가
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            String imageUrl = s3UploadService.saveFile(dto.getImageUrl());
            QnAPostImage image = new QnAPostImage();
            image.setImageUrl(imageUrl);
            image.setPost(post);
            post.addImage(image);
        }

        qnAPostRepository.save(post);

        List<String> tagNames = tagService.assignTagsToPost(post, dto.getTagIds());

        return QnAPostRespDto.fromEntity(post, tagNames, false);
    }

    // 게시글 수정 (작성자만 가능)
    @Transactional
    public QnAPostRespDto update(long id, UpdateQnARequestDto request, String email, String org) throws IOException {
        Member member = qnAMemberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAPost post = qnAPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        boolean isWriter = post.getWriter().getId().equals(member.getId());
      //  boolean isAdmin = member.getRole() == Role.ADMIN;

        if (!isWriter) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent());

        tagService.deleteTagsByPost(post); // 기존 태그 삭제
        List<String> tagNames = tagService.assignTagsToPost(post, request.getTagIds()); // 새 태그 등록

        // 이미지 수정 로직
        if (Boolean.TRUE.equals(request.getRemoveImage())) {
            // 이미지 삭제
            post.getImages().forEach(img -> {
                if (img.getImageUrl() != null) {
                    s3UploadService.deleteImage(img.getImageUrl());
                }
            });
            post.clearImages();

        } else if (request.getImage() != null && !request.getImage().isEmpty()) {
            // 이미지 교체/추가
            post.clearImages();
            String imageUrl = s3UploadService.saveFile(request.getImage());
            QnAPostImage image = new QnAPostImage();
            image.setImageUrl(imageUrl);
            image.setPost(post);
            post.addImage(image);
        } // 이미지 유지

        return QnAPostRespDto.fromEntity(post, tagNames, false);
    }

    // 게시글 삭제 (작성자, 관리자만 가능)
    @Transactional
    public void delete(long id, String email, String org){
        Member member = qnAMemberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAPost post = qnAPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        boolean isWriter = post.getWriter().getId().equals(member.getId());
        boolean isAdmin = member.getRole() == Role.ADMIN;

        if (!isWriter && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }
       // tagService.deleteTagsByPost(post);
        post.markAsDeleted(); //Deleted로 -> soft delete
    }

    // 게시글 전체 조회
    public PageResponseDTO<QnAPostRespDto> findAll(String org, Pageable pageable) {
        Page<QnAPost> page = qnAPostRepository.findAllByStatusAndWriter_Organization(Status.ACTIVE, org, pageable);
        List<QnAPost> posts = page.getContent();

        Map<Long, List<String>> tagMap = tagService.getTagNamesByPosts(posts);

        return PageResponseDTO.from(page, post -> {
            List<String> tagNames = tagMap.getOrDefault(post.getId(), List.of());
            // 전체 조회 시에는 스크랩 여부 false 값으로 고정
            return QnAPostRespDto.fromEntity(post, tagNames, false);
        });
    }


    // 게시글 상세 조회
    public QnAPostRespDto getPostById(Long id, String org, Long memberId) {
        QnAPost post = qnAPostRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!post.getWriter().getOrganization().equals(org)) {
            throw new AccessDeniedException("해당 조직의 게시글만 조회할 수 있습니다.");
        }

        post.increaseViewCount();
        List<String> tagNames = tagService.getTagNamesByPost(post);

        // 스크랩 여부 조회
        boolean isScrap = qnAScrapRepository.existsByQnaPostIdAndMemberId(id, memberId);

        return QnAPostRespDto.fromEntity(post, tagNames, isScrap);
    }

    // 게시글 신고하기
    @Transactional
    public String reportQnAPost(Long postId, Long userId) {
        QnAPost post = qnAPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Member member = qnAMemberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (qnAPostReportRepository.existsByMemberAndQnaPost(member, post)) {
            return "이미 신고한 게시글입니다.";
        }

        qnAPostReportRepository.save(QnAReport.builder()
                .member(member)
                .qnaPost(post)
                .build());

        post.increaseReportCount();

        return "게시글을 신고하였습니다.";
    }

    // 인기 3개
    @Transactional(readOnly = true)
    public List<QnAPopularPostRespDto> getPopularPosts(String organization) {
        return qnAPostRepository.findTop3ByStatusOrderByViewCountDescScrapCountDesc(Status.ACTIVE)
                .stream()
                .filter(post -> post.getWriter().getOrganization().equals(organization))
                .sorted(Comparator
                        .comparing(
                                (QnAPost post) -> post.getViewCount() == null ? 0L : post.getViewCount(),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                post -> post.getScrapCount() == null ? 0L : post.getScrapCount(),
                                Comparator.reverseOrder()
                        )
                )
                .limit(3)
                .map(QnAPopularPostRespDto::from)
                .toList();
    }


}
