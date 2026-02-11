package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tip_comment")
@EntityListeners(AuditingEntityListener.class)
public class TipComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_org_id", nullable = false)
    private MemberOrg writer;

    @ManyToOne
    @JoinColumn(name = "tip_id", nullable = false)
    private TipPost tipPost;

    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private ActiveStatus activeStatus;
    private int reportCount;

    public void updateContent(String content) {
        this.content = content;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }
}