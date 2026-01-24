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
public class FreePost implements ReportablePost {
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

    @Column(nullable = true)
    private String tag;

    @Enumerated(EnumType.STRING)
    private Status status;
    private int reportCount = 0;

    private Long viewCount = 0L;
    private Long scrapCount = 0L;

    @Column(nullable = false)
    private boolean isAnonymous;

    // FreeTagMap 연관관계 추가
    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreeTagMap> tagMaps = new ArrayList<>();

    // FreeReport 연관관계 추가
    @OneToMany(mappedBy = "freePost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FreeReport> reports = new ArrayList<>();

    // 이미지 연관관계 추가
    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreePostImage> images = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
    /*
    public void update(String title, String content, String tag) {
        this.title = title;
        this.content = content;

        if (tag == null || tag.trim().isEmpty()) {
            this.tag = null; // 태그 삭제
        } else {
            this.tag = tag; // 태그 추가 또는 수정
        }
    }
     */

    public void delete() {
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

    public void addImage(FreePostImage image) {
        images.add(image);
        image.setFreePost(this);
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
