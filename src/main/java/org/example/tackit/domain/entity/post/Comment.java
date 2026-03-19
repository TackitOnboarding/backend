package org.example.tackit.domain.entity.post;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "comment")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_org_id")
  private MemberOrg writer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private final List<Comment> children = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private ActiveStatus activeStatus = ActiveStatus.ACTIVE;

  @Column(nullable = false)
  private int reportCnt;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Builder
  public Comment(String content, Post post, MemberOrg writer, Comment parent) {
    this.content = content;
    this.post = post;
    this.writer = writer;
    this.parent = parent;
    this.reportCnt = 0;
  }

  public void addReply(Comment reply) {
    this.children.add(reply);
    reply.setParent(this);
  }

  // 관리자에 의한 활성화
  public void activate() {
    if (this.activeStatus != ActiveStatus.DELETED) {
      throw new IllegalStateException("삭제되지 않은 댓글은 활성화할 수 없습니다.");
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

  private void setParent(Comment parent) {
    this.parent = parent;
  }

  public void update(String content) {
    this.content = content;
  }

  public void delete() {
    this.activeStatus = ActiveStatus.DELETED;
  }
}