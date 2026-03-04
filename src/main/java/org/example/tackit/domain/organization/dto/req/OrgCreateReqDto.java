package org.example.tackit.domain.organization.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.OrgType;
import org.example.tackit.domain.entity.org.Organization;
import org.example.tackit.domain.entity.org.University;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgCreateReqDto {

  private String orgName;
  private OrgType orgType;
  private String orgDescription;

  private Long universityId;

  public Organization toEntity(University university) {
    return Organization.builder()
        .name(this.orgName)
        .type(this.orgType)
        .description(this.orgDescription)
        .university(this.orgType == OrgType.CLUB ? university : null)
        .build();
  }
}
