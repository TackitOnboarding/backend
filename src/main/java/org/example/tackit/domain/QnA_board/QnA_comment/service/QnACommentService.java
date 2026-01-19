package org.example.tackit.domain.QnA_board.QnA_comment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.QnA_board.QnA_comment.dto.request.QnACommentCreateDto;
import org.example.tackit.domain.QnA_board.QnA_comment.dto.request.QnACommentUpdateDto;
import org.example.tackit.domain.QnA_board.QnA_comment.dto.response.QnACommentResponseDto;
import org.example.tackit.domain.QnA_board.QnA_comment.repository.QnACommentRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAMemberRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnACommentService {
    private final QnACommentRepository qnACommentRepository;
    private final QnAPostRepository qnAPostRepository;
    private final QnAMemberRepository qnAMemberRepository;
    private final NotificationService notificationService;

    // 댓글 생성
    @Transactional
    public QnACommentResponseDto createComment(QnACommentCreateDto dto, String email, String org){
        Member member = qnAMemberRepository.findByEmailAndOrganization(email,org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAPost post = qnAPostRepository.findById(dto.getQnaPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        QnAComment comment = QnAComment.builder()
                .writer(member)
                .status(Status.ACTIVE)
                .qnAPost(post)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        // 댓글 DB 저장
        QnAComment savedComment = qnACommentRepository.save(comment);

        // 알림 전송
        if(!post.getWriter().getId().equals(member.getId())) {
            Member postWriter = post.getWriter();

            String message = member.getNickname() + "님이 글에 댓글을 남겼습니다.";

            String url = "/api/qna-post/" + post.getId();

            // 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(postWriter)
                    .type(NotificationType.COMMENT)
                    .message(message)
                    .relatedUrl(url)
                    .fromMemberId(member.getId())
                    .build();

            // 알림 저장 및 전송
            notificationService.send(notification);
        }
        return new QnACommentResponseDto(savedComment);
    }


    // 전체 댓글 조회
    @Transactional (readOnly = true)
    public List<QnACommentResponseDto> getCommentByPost(long postId, String org){
        QnAPost post = qnAPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getWriter().getOrganization().equals(org)) {
            throw new AccessDeniedException("해당 조직의 게시글만 조회할 수 있습니다.");
        }

        return qnACommentRepository.findByQnAPost(post)
                .stream()
                .map(QnACommentResponseDto::new)
                .toList();
    }

    // 댓글 수정 (작성자만 가능)
    @Transactional
    public QnACommentResponseDto updateComment(long commentId, QnACommentUpdateDto dto, String email, String org){
        Member member = qnAMemberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));


        QnAComment comment = qnACommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean isWriter = comment.getWriter().getId().equals(member.getId());

        if (!isWriter ) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        comment.updateContent(dto.getContent());

        return new QnACommentResponseDto(comment);
    }

    // 댓글 삭제 (작성자, 관리자만 가능)
    @Transactional
    public void deleteComment(long commentId, String email, String org){
        Member member = qnAMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAComment comment = qnACommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean isWriter = comment.getWriter().getId().equals(member.getId());
        boolean isAdmin = (member.getMemberRole() == MemberRole.ADMIN)
                && (member.getMemberType() == MemberType.ADMIN);

        if (!isWriter && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        qnACommentRepository.delete(comment); // hard delete
       // comment.markAsDeleted(); // soft delete
    }

    // 댓글 신고하기
    @Transactional
    public void increaseCommentReportCount(long id, String org) {
        QnAComment comment = qnACommentRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!comment.getWriter().getOrganization().equals(org)) {
            throw new AccessDeniedException("해당 조직의 댓글만 신고할 수 있습니다.");
        }
        comment.increaseReportCount();

        if (comment.getReportCount() >= 3) {
            qnACommentRepository.delete(comment); // hard delete
        }
    }

}
