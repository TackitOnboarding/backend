package org.example.tackit.domain.report.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.entity.ReportReason;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportLogDto {
    private Long reportId;
    private String reporterNickname; // 신고자
    private ReportReason reportReason;
    private LocalDateTime createdAt;

    public static ReportLogDto from(Report report) {
        return ReportLogDto.builder()
                .reportId(report.getId())
                .reporterNickname(report.getReporter().getNickname())
                .reportReason(report.getReportReason())
                .createdAt(report.getReportedAt())
                .build();
    }
}
