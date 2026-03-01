package org.example.tackit.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticePost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_org_id", nullable = false)
  private MemberOrg writer;

  private String title;

  @Lob
  private String content;

  private LocalDateTime createdAt;

  private Post type;

  @Builder.Default
  private Long viewCount = 0L;

  @Builder.Default
  private Long scrapCount = 0L;

  private boolean commentEnabled = true;

  @Version
  private Long version;

  // 이미지 연관관계 추가
  @OneToMany(mappedBy = "noticePost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<NoticePostImage> images = new ArrayList<>();

  public void update(String title, String content, boolean commentEnabled) {
    this.title = title;
    this.content = content;
    this.commentEnabled = commentEnabled;
  }

  public void increaseViewCount() {
    this.viewCount = (this.viewCount == null ? 0L : this.viewCount) + 1;
  }

  public void increaseScrapCount() {
    this.scrapCount = (this.scrapCount == null ? 0L : this.scrapCount) + 1;
  }

  public void decreaseScrapCount() {
    if (this.scrapCount != null && this.scrapCount > 0) {
      this.scrapCount -= 1;
    }
  }


}
