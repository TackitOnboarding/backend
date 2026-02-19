package org.example.tackit.domain.executive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReportedPostResDto {
    private Long reportId;
    private String reporter;
    private String writer;
    private String postType;
    private String reason;
    private LocalDateTime reportedAt;
    private ActiveStatus activeStatus;

    public static ReportedPostResDto from(Report post) {
        return new ReportedPostResDto(
                post.getId(),
                post.getReporter().getNickname(),
                post.getWriter().getNickname(),
                post.getPostType().name(),
                post.getReportReason().name(),
                post.getReportedAt(),
                post.getActiveStatus()
        );
    }
}
