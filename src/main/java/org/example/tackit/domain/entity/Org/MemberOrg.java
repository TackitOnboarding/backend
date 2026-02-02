package org.example.tackit.domain.entity.Org;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_club_nickname",
                        columnNames = {"club_id", "nickname"}
                ),
                @UniqueConstraint(
                        name = "uk_member_community_nickname",
                        columnNames = {"community_id", "nickname"}
                )
        }
)
public class MemberOrg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Enumerated(EnumType.STRING)
    private OrgType orgType; // CLUB, COMMUNITY

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole; // EXECUTIVE, GENERAL

    @Enumerated(EnumType.STRING)
    private MemberType memberType; // NEWBIE, SENIOR

    private String nickname;
    private String profileImageUrl;
    private int joinedYear;

    @Enumerated(EnumType.STRING)
    private OrgStatus orgStatus; // PENDING, ACTIVE, INACTIVE, REJECTED

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
