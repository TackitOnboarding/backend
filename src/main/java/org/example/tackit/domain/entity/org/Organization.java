package org.example.tackit.domain.entity.org;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "org_id")
  private Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  private OrgType type; // CLUB, COMMUNITY

  @Column(columnDefinition = "TEXT")
  private String description;

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  // Club 필드
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "university_id")
  private University university;
}


