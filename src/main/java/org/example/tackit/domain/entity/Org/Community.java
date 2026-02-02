package org.example.tackit.domain.entity.Org;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Community {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "org_id")
    private Organization organization;

    @Column(name = "community_name", nullable = false, unique = true)
    private String name; // 서비스 전체에서 유일한 이름

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
