package org.example.tackit.domain.entity.Org;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "university", indexes = {
        @Index(name = "idx_university_name", columnList = "university_name"),
        @Index(name = "idx_university_chosung", columnList = "university_chosung")
})
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long universityId;

    @Column(nullable = false, length = 100)
    private String universityName;

    @Column(length = 100)
    private String universityChosung; // 초성 저장 컬럼

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private BranchType branchType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(length = 255)
    private String address;
}
