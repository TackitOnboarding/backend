package org.example.tackit.domain.university.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.org.University;

@Getter
@Builder
public class UniversityRespDto {

  private Long id;
  private String name;
  private String regionName;

  public static UniversityRespDto from(University university) {
    return UniversityRespDto.builder()
        .id(university.getUniversityId())
        .name(university.getUniversityName())
        .regionName(university.getRegion().getRegionName())
        .build();
  }
}
