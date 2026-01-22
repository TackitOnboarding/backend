package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class NoticeScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_post_id", nullable = false)
    private NoticePost noticePost;

    @Column(nullable = false)
    private LocalDateTime savedAt;

    @Column(nullable = false)
    private Post type;

    @Builder
    public NoticeScrap(Member member, NoticePost noticePost, LocalDateTime savedAt) {
        this.member = member;
        this.noticePost = noticePost;
        this.savedAt = LocalDateTime.now();
        this.type = Post.Notice;
    }

}
