package org.example.tackit.domain.post.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPagingResDto {

  private List<PostSummaryResDto> posts;
  private PageInfo pageInfo;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PageInfo {

    private int currentPage;    // 현재 페이지 번호 (0부터 시작)
    private int size;           // 한 페이지당 게시글 수
    private long totalElements; // 전체 게시글 수
    private int totalPages;     // 전체 페이지 수
    private boolean isFirst;    // 첫 페이지 여부
    private boolean isLast;     // 마지막 페이지 여부
  }
}