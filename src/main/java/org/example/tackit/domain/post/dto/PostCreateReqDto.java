package org.example.tackit.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.post.PostCategory;
import org.example.tackit.domain.entity.post.PostType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateReqDto {

  @NotNull(message = "게시글 타입은 필수입니다.")
  private PostType postType;

  private PostCategory postCategory;

  @NotBlank(message = "제목을 입력해주세요.")
  private String title;

  @NotBlank(message = "내용을 입력해주세요.")
  private String content;

  private Boolean isAnonymous;
  private Boolean commentEnabled;
}