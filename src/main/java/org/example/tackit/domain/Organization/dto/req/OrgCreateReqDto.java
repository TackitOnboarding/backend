package org.example.tackit.domain.Organization.dto.req;

import lombok.Data;
import lombok.Getter;
import org.example.tackit.domain.entity.Org.*;

@Data
@Getter
public class OrgCreateReqDto {
    private String orgName;
    private OrgType orgType;

    private Long schoolId;
    private String orgDescription;

    // Club 엔티티 변환
    public Club toClub(School school, Organization organization) {
        return Club.builder()
                .organization(organization)
                .name(this.orgName)
                .school(school)
                .description(this.orgDescription)
                .build();
    }

    // Community 엔티티 변환
    public Community toCommunity(Organization organization) {
        return Community.builder()
                .organization(organization)
                .name(this.orgName)
                .description(this.orgDescription)
                .build();
    }
}
