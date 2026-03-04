package org.example.tackit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;

@Entity
@Getter
@NoArgsConstructor
public class TipScrap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_org_id", nullable = false)
  private MemberOrg memberOrg;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tip_id", nullable = false)
  private TipPost tipPost;

  @Column(name = "saved_at", nullable = false)
  private LocalDateTime savedAt;

  private final Post type = Post.Tip;

  @Builder
  public TipScrap(MemberOrg memberOrg, TipPost tipPost, LocalDateTime savedAt) {
    this.memberOrg = memberOrg;
    this.tipPost = tipPost;
    this.savedAt = LocalDateTime.now();
  }
}
