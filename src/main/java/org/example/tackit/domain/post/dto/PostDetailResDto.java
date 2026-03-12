package org.example.tackit.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResDto {

  private PostInfo post;
  private int totalCommentCount;
  private List<CommentResDto> comments;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PostInfo {

    private Long id;
    private String title;
    private SimpleMemberProfileDto writer;
    private String content;
    private Boolean isAnonymous;
    private Boolean isMine;
    private Boolean isScrap;
    private int viewCount;
    private LocalDateTime createdAt;
  }
}