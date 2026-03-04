package org.example.tackit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "qna_comment")
public class QnAComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_org_id", nullable = false)
  private MemberOrg writer;

  @ManyToOne
  @JoinColumn(name = "qna_id", nullable = false)
  private QnAPost qnAPost;

  private String content;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  private ActiveStatus activeStatus;
  private int reportCount;

  public void updateContent(String content) {
    this.content = content;
  }

  public void increaseReportCount() {
    this.reportCount++;
  }
}
