package org.example.tackit.domain.post.dto;

public record PostIdResDto(Long postId) {

  public static PostIdResDto from(Long postId) {
    return new PostIdResDto(postId);
  }
}