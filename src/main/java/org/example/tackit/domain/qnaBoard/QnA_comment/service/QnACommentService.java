package org.example.tackit.domain.qnaBoard.QnA_comment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.qnaBoard.QnA_comment.dto.request.QnACommentCreateDto;
import org.example.tackit.domain.qnaBoard.QnA_comment.dto.request.QnACommentUpdateDto;
import org.example.tackit.domain.qnaBoard.QnA_comment.dto.response.QnACommentResponseDto;
import org.example.tackit.domain.qnaBoard.QnA_comment.repository.QnACommentRepository;
import org.example.tackit.domain.qnaBoard.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.entity.Org.MemberOrg;
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
  private final MemberOrgRepository memberOrgRepository;
  private final NotificationService notificationService;

  // 댓글 생성
  @Transactional
  public QnACommentResponseDto createComment(QnACommentCreateDto dto, String email, Long orgId) {
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    QnAPost post = qnAPostRepository.findById(dto.getQnaPostId())
        .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

    QnAComment comment = QnAComment.builder()
        .writer(member)
        .activeStatus(ActiveStatus.ACTIVE)
        .qnAPost(post)
        .content(dto.getContent())
        .createdAt(LocalDateTime.now())
        .build();

    // 댓글 DB 저장
    QnAComment savedComment = qnACommentRepository.save(comment);

    // 알림 전송
    if (!post.getWriter().getId().equals(member.getId())) {
      MemberOrg postWriter = post.getWriter();

      notificationService.send(Notification.builder()
          .member(postWriter.getMember()) // 수신자 계정
          .memberOrgId(postWriter.getId()) // 수신자 프로필 ID
          .type(NotificationType.COMMENT)
          .message(member.getNickname() + "님이 QnA 글에 댓글을 남겼습니다.")
          .relatedUrl("/api/qna-posts/" + post.getId())
          .fromMemberOrgId(member.getId()) // 보낸 사람 프로필 ID
          .build());
    }
    return new QnACommentResponseDto(savedComment);

  }

  // 전체 댓글 조회
  @Transactional(readOnly = true)
  public List<QnACommentResponseDto> getCommentByPost(Long postId, Long orgId) {
    QnAPost post = qnAPostRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

    if (!post.getWriter().getId().equals(orgId)) {
      throw new AccessDeniedException("해당 조직의 게시글만 조회할 수 있습니다.");
    }

    return qnACommentRepository.findByQnAPost(post)
        .stream()
        .map(QnACommentResponseDto::new)
        .toList();
  }

  // 댓글 수정 (작성자만 가능)
  @Transactional
  public QnACommentResponseDto updateComment(Long commentId, QnACommentUpdateDto dto, String email,
      Long orgId) {
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    QnAComment comment = qnACommentRepository.findById(commentId)
        .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

    boolean isWriter = comment.getWriter().getId().equals(member.getId());

    if (!isWriter) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }

    comment.updateContent(dto.getContent());

    return new QnACommentResponseDto(comment);
  }

  // 댓글 삭제 (작성자, 관리자만 가능)
  @Transactional
  public void deleteComment(Long commentId, String email, Long orgId) {
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
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
  }

  // 댓글 신고하기
  @Transactional
  public void increaseCommentReportCount(Long id, Long orgId) {
    QnAComment comment = qnACommentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

    if (!comment.getWriter().getId().equals(orgId)) {
      throw new AccessDeniedException("해당 조직의 댓글만 신고할 수 있습니다.");
    }
    comment.increaseReportCount();

    if (comment.getReportCount() >= 3) {
      qnACommentRepository.delete(comment); // hard delete
    }
  }

}
