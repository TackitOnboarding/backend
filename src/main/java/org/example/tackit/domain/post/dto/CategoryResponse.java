package org.example.tackit.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.post.PostCategory;

@Getter
@AllArgsConstructor
public class CategoryResponse {

  private String key;
  private String label;

  public static CategoryResponse from(PostCategory category) {
    return new CategoryResponse(category.name(), category.getDescription());
  }
}