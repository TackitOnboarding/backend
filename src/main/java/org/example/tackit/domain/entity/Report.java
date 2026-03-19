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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.post.PostType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  // POST 혹은 COMMENT의 id
  @Column(nullable = false)
  private Long targetId;

  // POST / COMMENT
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TargetType targetType;

  // 타겟 대상 postId comment면 comment가 달린 post의 id, post는 본인.
  @Column(nullable = false)
  private Long postId;

  // 게시판
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private PostType postType;

  // 신고 당시 게시글 제목
  @Column(nullable = false, length = 100)
  private String reportedPostTitle;

  // 신고 당시 타겟 게시글/댓글 내용
  @Column(columnDefinition = "LONGTEXT", nullable = false)
  private String reportedContent;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportReason reportReason;

  @Column(columnDefinition = "TEXT")
  private String detailReason;

  @CreatedDate
  private LocalDateTime reportedAt;

  @Enumerated(EnumType.STRING)
  private ActiveStatus activeStatus;

  @Builder
  public Report(MemberOrg reporter, MemberOrg targetMember, Long targetId, TargetType targetType,
      Long postId, PostType postType, String reportedPostTitle, String reportedContent,
      ReportReason reportReason, String detailReason, ActiveStatus activeStatus) {
    this.reporter = reporter;
    this.targetMember = targetMember;
    this.targetId = targetId;
    this.targetType = targetType;
    this.postId = postId;
    this.postType = postType;
    this.reportedPostTitle = reportedPostTitle;
    this.reportedContent = reportedContent;
    this.reportReason = reportReason;
    this.detailReason = detailReason;
    this.activeStatus = (activeStatus != null) ? activeStatus : ActiveStatus.ACTIVE;
  }
}

