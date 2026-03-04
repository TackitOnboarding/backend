package org.example.tackit.domain.entity.org;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "region")
public class Region {

  @Id
  private Integer regionId;

  @Column(unique = true, nullable = false, length = 50)
  private String regionName;

  @Column(unique = true, nullable = false, length = 10)
  private String sidoCode;
}
