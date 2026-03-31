package org.example.tackit.domain.entity.post;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.Organization;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "post")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostType postType; // 'free', 'qna', 'tip'

  @Enumerated(EnumType.STRING)
  private PostCategory category;

  @Column(nullable = false)
  private String title;

  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActiveStatus activeStatus;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private boolean isAnonymous; // 익명 여부 (공지사항은 false)

  @Column(nullable = false)
  private boolean commentEnabled = true; // 댓글 허용 여부 (공지사항은 false)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_org_id", nullable = false)
  private MemberOrg writer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "org_id", nullable = false)
  private Organization organization;

  @Column(nullable = false)
  private int reportCnt;

  @Column(nullable = false)
  private int viewCnt;

  @Column(nullable = false)
  private int scrapCnt;

  @Version
  private Long version;

  // 연관관계 설정
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Scrap> scraps = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<PostImage> images = new ArrayList<>();

  @Builder
  public Post(PostType postType, PostCategory category, MemberOrg writer, Organization organization,
      String title, String content, Boolean isAnonymous, Boolean commentEnabled) {
    this.postType = postType;
    this.category = category;
    this.writer = writer;
    this.organization = organization;
    this.title = title;
    this.content = content;
    this.isAnonymous = (isAnonymous != null) ? isAnonymous : false;
    this.commentEnabled = (commentEnabled != null) ? commentEnabled : true;
    this.activeStatus = ActiveStatus.ACTIVE;
    this.viewCnt = 0;
    this.scrapCnt = 0;
    this.reportCnt = 0;
  }

  // 비즈니스 로직 메서드
  public void update(String title, String content, PostCategory category, Boolean isAnonymous,
      Boolean commentEnabled) {
    if (title != null) {
      this.title = title;
    }
    if (content != null) {
      this.content = content;
    }
    if (category != null) {
      this.category = category;
    }
    if (isAnonymous != null) {
      this.isAnonymous = isAnonymous;
    }
    if (commentEnabled != null) {
      this.commentEnabled = commentEnabled;
    }
  }

  public void delete() {
    this.activeStatus = ActiveStatus.DELETED;
  }

  public void increaseReportCount() {
    this.reportCnt++;
    if (this.reportCnt >= 3) {
      this.delete();
    }
  }

  // 관리자에 의한 활성화
  public void activate() {
    if (this.activeStatus != ActiveStatus.DELETED) {
      throw new IllegalStateException("삭제되지 않은 게시글은 활성화할 수 없습니다.");
    }
    this.activeStatus = ActiveStatus.ACTIVE;
    this.reportCnt = 0;
  }

  public void receiveReport() {
    this.reportCnt++;

    if (this.reportCnt >= 3) {
      this.activeStatus = ActiveStatus.DELETED;
    }
  }

  public void increaseViewCnt() {
    this.viewCnt++;
  }

  public void increaseScrapCnt() {
    this.scrapCnt++;
  }

  public void decreaseScrapCnt() {
    if (this.scrapCnt > 0) {
      this.scrapCnt--;
    }
  }

  public void addImage(PostImage image) {
    this.images.add(image);
    image.setPost(this);
  }

  public void clearImages() {
    this.images.forEach(image -> image.setPost(null));
    this.images.clear();
  }
}