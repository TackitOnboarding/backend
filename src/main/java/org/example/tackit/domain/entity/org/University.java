package org.example.tackit.domain.entity.org;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
