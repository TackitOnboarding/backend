package org.example.tackit.domain.entity.poll;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "poll_target",
    indexes = @Index(name = "idx_poll_target_poll_member", columnList = "poll_id, member_org_id"))
public class PollTarget {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_target_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", nullable = false)
  private Poll poll;

  @Column(nullable = false)
  private Long memberOrgId;

  @Builder
  public PollTarget(Poll poll, Long memberOrgId) {
    this.poll = poll;
    this.memberOrgId = memberOrgId;
  }
}