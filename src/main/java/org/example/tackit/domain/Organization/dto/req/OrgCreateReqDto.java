package org.example.tackit.domain.Organization.dto.req;

import lombok.*;
import org.example.tackit.domain.entity.Org.*;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgCreateReqDto {
    private String orgName;
    private OrgType orgType;
    private String orgDescription;

    private Long schoolId;

    public Organization toEntity(School school) {
        return Organization.builder()
                .name(this.orgName)
                .type(this.orgType)
                .description(this.orgDescription)
                .school(this.orgType == OrgType.CLUB ?  school : null)
                .build();
    }
}
