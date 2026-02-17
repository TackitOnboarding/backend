package org.example.tackit.domain.entity.poll;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "poll")
public class Poll {

  @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<PollOption> options = new ArrayList<>();
  @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<PollTarget> targets = new ArrayList<>();
  @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<PollParticipant> participants = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_id")
  private Long id;

  @Column(nullable = false)
  private Long orgId;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PollType pollType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PollScope scope;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PollStatus status;

  private LocalDateTime endsAt; // 마감 시간 NULL = 무기한

  @Column(nullable = false)
  private boolean isMulti;

  @Column(nullable = false)
  private boolean isAnonymous;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member creator;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Builder
  public Poll(Long orgId, String title, PollType pollType,
      PollScope scope, LocalDateTime endsAt, boolean isMulti, boolean isAnonymous, Member creator) {
    this.orgId = orgId;
    this.title = title;
    this.pollType = pollType;
    this.scope = scope;
    this.endsAt = endsAt;
    this.isMulti = isMulti;
    this.isAnonymous = isAnonymous;
    this.creator = creator;
    this.status = PollStatus.ONGOING;
  }

  // 투표 수정
  public void update(String title, LocalDateTime endsAt, Boolean isMulti) {
    if (title != null && !title.isBlank()) {
      this.title = title;
    }

    if (endsAt != null) {
      this.endsAt = endsAt;
    }

    if (isMulti != null) {
      this.isMulti = isMulti;
    }
  }

  // 투표 강제 마감
  public void close() {
    this.status = PollStatus.ENDED;
  }
}