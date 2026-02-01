package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class FreeScrap {
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
    private LocalDateTime savedAt;

    @Column(nullable = false)
    private Post type;

    @Builder
    public FreeScrap(MemberOrg member, FreePost freePost, LocalDateTime savedAt) {
        this.member = member;
        this.freePost = freePost;
        this.savedAt = LocalDateTime.now();
        this.type = Post.Free;
    }


}
