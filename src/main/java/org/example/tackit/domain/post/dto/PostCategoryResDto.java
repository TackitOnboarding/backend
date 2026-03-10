package org.example.tackit.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.post.PostCategory;

@Getter
@AllArgsConstructor
public class PostCategoryResDto {

  private String key;
  private String label;

  public static PostCategoryResDto from(PostCategory category) {
    return new PostCategoryResDto(category.name(), category.getDescription());
  }
}