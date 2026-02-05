package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.report.dto.ReportRequestDto;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private MemberOrg reporter;

    private Long targetId;

    @Enumerated(EnumType.STRING)  // DB에 문자열 형태로 저장
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    private LocalDateTime createdAt = LocalDateTime.now();

    public static Report fromDto(ReportRequestDto dto, MemberOrg reporter) {
        Report report = new Report();
        report.reporter = reporter;
        report.targetId = dto.getTargetId();
        report.targetType = dto.getTargetType();
        report.reason = dto.getReason();
        report.createdAt = LocalDateTime.now();
        return report;
    }
}

