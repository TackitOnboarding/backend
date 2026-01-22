package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    private String title;

    @Lob
    private String content;
    private LocalDateTime createdAt;
    private Post type;
    private String organization;

    private Long viewCount = 0L;
    private Long scrapCount = 0L;

    // 이미지 연관관계 추가
    @OneToMany(mappedBy = "noticePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticePostImage> images = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addImage(NoticePostImage image) {
        images.add(image);
        image.setNoticePost(this);
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
