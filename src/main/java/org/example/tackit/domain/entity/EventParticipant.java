package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "event_participant")
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_org_id", nullable = false)
    private MemberOrg memberOrg;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public EventParticipant(Event event, MemberOrg memberOrg) {
        this.event = event;
        this.memberOrg = memberOrg;
    }

    // 연관관계 편의 메서드
    public void assignEvent(Event event) {
        this.event = event;
        event.getParticipants().add(this);
    }
}