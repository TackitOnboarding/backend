package org.example.tackit.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.post.Comment;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.post.dto.CommentCreateReqDto;
import org.example.tackit.domain.post.dto.CommentUpdateReqDto;
import org.example.tackit.domain.post.repository.CommentRepository;
import org.example.tackit.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final MemberOrgValidator memberOrgValidator;

  /**
   * 댓글 작성
   */
  @Transactional
  public Long createComment(Long memberOrgId, Long postId, CommentCreateReqDto reqDto) {
    MemberOrg writer = memberOrgValidator.validateActiveMembership(memberOrgId);

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

    Comment parent = null;
    if (reqDto.getParentCommentId() != null) {
      parent = findCommentById(reqDto.getParentCommentId());

      if (!parent.getPost().getId().equals(postId)) {
        throw new IllegalArgumentException("부모 댓글과 같은 게시글에만 대댓글을 작성할 수 있습니다.");
      }
    }

    Comment comment = Comment.builder()
        .post(post)
        .writer(writer)
        .parent(parent) // 대댓글이 아니면 null
        .content(reqDto.getContent())
        .build();

    if (parent != null) {
      parent.addReply(comment);
    }

    return commentRepository.save(comment).getId();
  }

  /**
   * 댓글 수정
   */
  @Transactional
  public void updateComment(Long memberOrgId, Long commentId, CommentUpdateReqDto reqDto) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    Comment comment = findCommentById(commentId);

    validateCommentWriter(comment, memberOrgId);

    comment.update(reqDto.getContent());
  }

  /**
   * 댓글 삭제
   */
  @Transactional
  public void deleteComment(Long memberOrgId, Long commentId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    Comment comment = findCommentById(commentId);

    validateCommentWriter(comment, memberOrgId);

    comment.delete();
  }

  /**
   * 댓글 존재 확인 및 조회 메서드
   */
  private Comment findCommentById(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
  }

  /**
   * 댓글 작성자 권한 검증
   */
  private void validateCommentWriter(Comment comment, Long memberOrgId) {
    if (!comment.getWriter().getId().equals(memberOrgId)) {
      throw new IllegalArgumentException("작성자만 수정/삭제할 수 있습니다.");
    }
  }
}