package org.example.tackit.domain.entity.poll;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "poll_participant",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_poll_participant_poll_member",
            columnNames = {"poll_id", "member_org_id"}
        )
    })
public class PollParticipant {

  @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Vote> votes = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_participant_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", nullable = false)
  private Poll poll;

  @Column(nullable = false)
  private Long memberOrgId;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Builder
  public PollParticipant(Poll poll, Long memberOrgId) {
    this.poll = poll;
    this.memberOrgId = memberOrgId;
  }

  // 재투표 시 updatedAt 업데이트 용
  public void reVote() {
    this.updatedAt = LocalDateTime.now();
  }
}