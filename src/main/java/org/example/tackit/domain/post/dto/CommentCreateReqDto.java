package org.example.tackit.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateReqDto {

  private Long parentCommentId;

  @NotBlank(message = "댓글 내용을 입력해주세요.")
  private String content;
}