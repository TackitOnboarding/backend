package org.example.tackit.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.post.PostCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateReqDto {

  private PostCategory postCategory;

  @NotBlank(message = "제목을 입력해주세요.")
  private String title;

  @NotBlank(message = "내용을 입력해주세요.")
  private String content;

  private Boolean isAnonymous;
  private Boolean commentEnabled;
}