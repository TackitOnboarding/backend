package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.admin.model.ReportablePost;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "qna_post")
public class QnAPost implements ReportablePost {
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

    @Enumerated(EnumType.STRING)
    private Status status;
    private int reportCount;

    private Long viewCount = 0L;
    private Long scrapCount = 0L;

    @Column(nullable = false)
    private boolean isAnonymous;

    // QnATagMap 연관관계 추가
    @OneToMany(mappedBy = "qnaPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QnATagMap> tagMaps = new ArrayList<>();

    // QnAReport 연관관계 추가
    @OneToMany(mappedBy = "qnaPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<QnAReport> reports = new ArrayList<>();

    // 이미지 연관관계 추가
    @OneToMany(mappedBy = "qnaPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QnAPostImage> images = new ArrayList<>();

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void markAsDeleted() {
        this.status = Status.DELETED;
    }

    public void increaseReportCount() {
        this.reportCount++;
        if (this.reportCount >= 3) {
            this.status = Status.DELETED;
        }
    }

    public void activate(){
        if (this.status != Status.DELETED) {
            throw new IllegalStateException("삭제되지 않은 게시글은 활성화할 수 없습니다.");
        }

        this.status = Status.ACTIVE;
        this.reportCount = 0;
    }

    public void addImage(QnAPostImage image) {
        images.add(image);
        image.setPost(this);
    }

    public void clearImages() {
        images.forEach(img -> img.setPost(null));
        images.clear();
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
