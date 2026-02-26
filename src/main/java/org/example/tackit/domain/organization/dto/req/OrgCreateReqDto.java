package org.example.tackit.domain.organization.dto.req;

import lombok.*;
import org.example.tackit.domain.entity.Org.*;
import org.example.tackit.domain.entity.Org.University;

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
                .university(this.orgType == OrgType.CLUB ?  university : null)
                .build();
    }
}
