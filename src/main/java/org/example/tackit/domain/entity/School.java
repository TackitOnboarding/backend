package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Enumerated(EnumType.STRING)
    private SchoolType schoolType; // 본교, 분교

    @Column(nullable = false)
    private Integer regionId; // 공공데이터 지역 코드

    private String address;

    // 연합 여부를 판단하는 편의 메서드
    public boolean isUnion() {
        return "연합/전국".equals(this.schoolName);
    }
}
