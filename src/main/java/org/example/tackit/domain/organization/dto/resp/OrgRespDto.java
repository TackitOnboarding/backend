package org.example.tackit.domain.organization.dto.resp;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.org.OrgType;
import org.example.tackit.domain.entity.org.Organization;

@Getter
@Builder
public class OrgRespDto {

  private Long orgId;
  private String orgName;
  private String orgType;
  private String universityName; // CLUB일 때만 값이 있음
  private String createdAt;

  public static OrgRespDto of(Organization org) {
    String universityName = (org.getType() == OrgType.CLUB && org.getUniversity() != null)
        ? org.getUniversity().getUniversityName()
        : null;

    return OrgRespDto.builder()
        .orgId(org.getId())
        .orgName(org.getName()) // 동아리 이름 또는 소모임 이름
        .orgType(org.getType().name())
        .universityName(universityName)
        .createdAt(org.getCreatedAt() != null ? org.getCreatedAt().toString() : "")
        .build();
  }
}
