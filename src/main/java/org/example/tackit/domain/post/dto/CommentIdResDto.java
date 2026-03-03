package org.example.tackit.domain.post.dto;

public record CommentIdResDto(Long commentId) {

  public static CommentIdResDto from(Long commentId) {
    return new CommentIdResDto(commentId);
  }
}