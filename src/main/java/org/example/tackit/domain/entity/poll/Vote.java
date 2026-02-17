package org.example.tackit.domain.entity.poll;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "vote")
public class Vote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vote_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_participant_id", nullable = false)
  private PollParticipant participant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_option_id", nullable = false)
  private PollOption pollOption;

  @Builder
  public Vote(PollParticipant participant, PollOption pollOption) {
    this.participant = participant;
    this.pollOption = pollOption;
  }
}