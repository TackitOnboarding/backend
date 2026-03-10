package org.example.tackit.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.post.PostCategory;

@Getter
@AllArgsConstructor
public class PostCategoryResponseDto {

  private String key;
  private String label;

  public static PostCategoryResponseDto from(PostCategory category) {
    return new PostCategoryResponseDto(category.name(), category.getDescription());
  }
}