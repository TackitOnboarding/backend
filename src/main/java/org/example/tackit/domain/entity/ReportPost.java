package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Org.MemberOrg;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report_post")
public class ReportPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private MemberOrg reporter;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private MemberOrg writer;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Post postType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    // 비활성화 여부
    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus;

    @Builder
    public ReportPost(MemberOrg reporter, MemberOrg writer, Long targetId,
                      Post postType, ReportReason reportReason, ActiveStatus activeStatus) {
        this.reporter = reporter;
        this.writer = writer;
        this.targetId = targetId;
        this.postType = postType;
        this.reportReason = reportReason;
        this.reportedAt = LocalDateTime.now();
        this.activeStatus = activeStatus != null ? activeStatus : ActiveStatus.ACTIVE;
    }
}
