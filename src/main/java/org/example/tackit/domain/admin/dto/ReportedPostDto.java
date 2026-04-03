package org.example.tackit.domain.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.entity.ReportReason;
import org.example.tackit.domain.entity.TargetType;

@Getter
@AllArgsConstructor
@Builder
public class ReportedPostDto {
  private Long id;
  private String title; // 신고 당시의 제목
  private TargetType targetType;
  private ReportReason reason;
  private ActiveStatus status;
  private int reportCount;
  private LocalDateTime reportedAt;

  public static ReportedPostDto from(Report report, int reportCount, ActiveStatus activeStatus) {
    return ReportedPostDto.builder()
            .id(report.getId())
            .title(report.getReportedPostTitle())
            .targetType(report.getTargetType())
            .reason(report.getReportReason())
            .status(activeStatus)
            .reportCount(reportCount)
            .reportedAt(report.getReportedAt())
            .build();
  }

  /*
  public static ReportedPostDTO from(Post post) {
    return ReportedPostDTO.builder()
            .id(post.getId())
            .reportedPostTitle(post.getTitle()) // 게시글 제목
            .reportCnt(post.getReportCnt())     // 신고 횟수
            .activeStatus(post.getActiveStatus())
            // .reportReason(...) <- 만약 Post에 사유가 없다면 우선 비워두거나
            //                       첫 번째 신고 사유를 가져오는 로직 추가
            .build();
  }

   */
}
