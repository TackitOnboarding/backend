package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Org.Organization;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event")
public class Event{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "color_chip", nullable = false)
    private String colorChip;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_scope", nullable = false)
    private EventScope eventScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member creator;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> participants = new ArrayList<>();

    @Builder
    public Event(String title, LocalDateTime startsAt, LocalDateTime endsAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                 String description, String colorChip, EventScope eventScope, 
                 Organization organization, Member creator) {
        this.title = title;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.colorChip = colorChip;
        this.eventScope = eventScope;
        this.organization = organization;
        this.creator = creator;
    }

    public void update(String title, LocalDateTime startsAt, LocalDateTime endsAt,
                       String description, String colorChip, EventScope eventScope) {
        if (title != null) this.title = title;
        if (startsAt != null) this.startsAt = startsAt;
        if (endsAt != null) this.endsAt = endsAt;
        if (description != null) this.description = description;
        if (colorChip != null) this.colorChip = colorChip;
        if (eventScope != null) this.eventScope = eventScope;
    }

    // 참여자 목록 초기화 (참여자 수정 시 사용)
    public void clearParticipants() {
        this.participants.clear();
    }
}