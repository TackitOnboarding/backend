package org.example.tackit.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.entity.ReportReason;
import org.example.tackit.domain.entity.TargetType;
import org.example.tackit.domain.entity.post.Post;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportedPostDetailDto {
    private Long postId;
    private String postLink;       // 게시글 상세 페이지 링크
    private String title;
    private String content;        // 신고 당시 게시글 내용
    private TargetType targetType;
    private ReportReason reason;
    private String detailReason;   // 상세 신고 사유
    private ActiveStatus status;
    private int reportCnt;
    private LocalDateTime reportedAt;


    private String reporterNickname; // 신고자
    private String writerNickname;   // 작성자

    public static ReportedPostDetailDto from(Report report, Post post) {
        return ReportedPostDetailDto.builder()
                .postId(post.getId())
                .postLink("/api/posts/" + post.getId())
                .title(report.getReportedPostTitle())
                .content(report.getReportedContent())
                .targetType(report.getTargetType())
                .reason(report.getReportReason())
                .detailReason(report.getDetailReason())
                .status(post.getActiveStatus())
                .reportCnt(post.getReportCnt())
                .reportedAt(report.getReportedAt())
                .reporterNickname(report.getReporter().getNickname()) // MemberOrg 참조
                .writerNickname(report.getTargetMember().getNickname()) // MemberOrg 참조
                .build();
    }
}
