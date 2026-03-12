package org.example.tackit.domain.executive.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;

@Getter
@AllArgsConstructor
public class ReportedPostResDto {

  private Long reportId;
  private String reporter;
  private String writer;
  private String reason;
  private LocalDateTime reportedAt;
  private ActiveStatus activeStatus;

  public static ReportedPostResDto from(Report post) {
    return new ReportedPostResDto(
        post.getId(),
        post.getReporter().getNickname(),
        post.getTargetMember().getNickname(),
        post.getReportReason().name(),
        post.getReportedAt(),
        post.getActiveStatus()
    );
  }
}
