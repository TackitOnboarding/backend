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
@Table(name = "poll_option")
public class PollOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_option_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", nullable = false)
  private Poll poll;

  @Column(nullable = false)
  private String content; // 항목 내용

  private Integer seq;    // 정렬 순서

  @Builder
  public PollOption(Poll poll, String content, Integer seq) {
    this.poll = poll;
    this.content = content;
    this.seq = seq;
  }
}