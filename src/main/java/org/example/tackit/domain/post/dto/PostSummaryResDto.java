package org.example.tackit.domain.post.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryResDto {

  private Long id;
  private String title;
  private String contentSummary;
  private CategoryResponse postCategory;
  private String thumbnail;
  private SimpleMemberProfileDto writer;
  private LocalDateTime createdAt;
  private Boolean isAnonymous;
  private int viewCount;
}