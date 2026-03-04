package org.example.tackit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.report.dto.ReportRequestDto;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "report")
public class Report {

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

  // POST / COMMENT
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TargetType targetType;

  // QNA / TIP / FREE
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Post postType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportReason reportReason;

  @Column(nullable = false)
  private LocalDateTime reportedAt;

  @Enumerated(EnumType.STRING)
  private ActiveStatus activeStatus;

  public static Report from(ReportRequestDto dto, MemberOrg reporter, MemberOrg writer) {
    return Report.builder()
        .reporter(reporter)
        .writer(writer)
        .targetId(dto.getTargetId())
        .targetType(dto.getTargetType())
        .postType(dto.getPostType())
        .reportReason(dto.getReason())
        .activeStatus(ActiveStatus.ACTIVE) // 초기값 설정
        .build();
  }

}

