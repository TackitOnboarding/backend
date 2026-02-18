package org.example.tackit.domain.poll.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PollSidebarResDto {

  private List<PollSummaryDto> urgentVotes; // 마감 임박 (없으면 null or 빈 리스트)
  private List<PollSummaryDto> votes;       // 진행 중인 투표 목록

  @Getter
  @Builder
  public static class PollSummaryDto {

    private Long id;
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endsAt;

    private int participationCount;
    private int targetMemberCount;
    private boolean isVoted;
  }
}