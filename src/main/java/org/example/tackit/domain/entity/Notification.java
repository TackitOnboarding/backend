package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 알림마다 Member 조회하는 N+1 쿼리 방지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(nullable = false)
    private Long memberOrgId;

    // 알림 유형
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // 어떤 프로필 알림인지
    @Column(nullable = false)
    private Long fromMemberOrgId;

    @Column(nullable = false)
    private String message;

    private String relatedUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }
}
