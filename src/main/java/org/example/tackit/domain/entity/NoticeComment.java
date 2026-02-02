package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice_comment")
@EntityListeners(AuditingEntityListener.class)
public class NoticeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_org_id", nullable = false)
    private MemberOrg writer;

    @ManyToOne
    @JoinColumn(name = "notice_id", nullable = false)
    // 게시글 Id
    private NoticePost noticePost;

    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private int reportCount;

    @Builder
    public NoticeComment(MemberOrg writer, NoticePost noticePost, String content) {
        this.writer = writer;
        this.noticePost = noticePost;
        this.content = content;
        this.reportCount = 0;
    }

    public void updateContent(String content) { this.content = content; }
    public void increaseReportCount() {
        this.reportCount++;
    }



}
