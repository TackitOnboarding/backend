package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "free_report")
public class FreeReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_org_id", nullable = false)
    private MemberOrg member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_post_id", nullable = false)
    private FreePost freePost;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Column(nullable = false)
    private Post type;

    @Builder
    public FreeReport(MemberOrg member, FreePost freePost) {
        this.member = member;
        this.freePost = freePost;
        this.reportedAt = LocalDateTime.now();
        this.type = Post.Free;
    }
}
