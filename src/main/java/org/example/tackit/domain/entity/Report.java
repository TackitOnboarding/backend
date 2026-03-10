package org.example.tackit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
  @JoinColumn(name = "targetMember_id", nullable = false)
  private MemberOrg targetMember;

  @Column(nullable = false)
  private Long targetId;

  // POST / COMMENT
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TargetType targetType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportReason reportReason;

  @Column(columnDefinition = "TEXT")
  private String customReason;

  @CreatedDate
  private LocalDateTime reportedAt;

  @Enumerated(EnumType.STRING)
  private ActiveStatus activeStatus;

  //TODO Entity 안에 DTO를 참조하는 메소드가 있는 것이 이상함. 분리해야할듯
  public static Report from(ReportRequestDto dto, MemberOrg reporter, MemberOrg targetMember) {
    return Report.builder()
        .reporter(reporter)
        .targetMember(targetMember)
        .targetId(dto.getTargetId())
        .targetType(dto.getTargetType())
        .reportReason(dto.getReason())
        .activeStatus(ActiveStatus.ACTIVE) // 초기값 설정
        .build();
  }

}

