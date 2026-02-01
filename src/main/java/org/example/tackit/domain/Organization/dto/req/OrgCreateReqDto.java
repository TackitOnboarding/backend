package org.example.tackit.domain.Organization.dto.req;

import lombok.Data;
import lombok.Getter;
import org.example.tackit.domain.entity.Club;
import org.example.tackit.domain.entity.Community;
import org.example.tackit.domain.entity.OrgType;
import org.example.tackit.domain.entity.School;

@Data
@Getter
public class OrgCreateReqDto {
    private String orgName;
    private OrgType orgType;

    private Long schoolId;
    private String orgDescription;

    // Club 엔티티 변환
    public Club toClub(School school) {
        return Club.builder()
                .name(this.orgName)
                .school(school)
                .description(this.orgDescription)
                .build();
    }

    // Community 엔티티 변환
    public Community toCommunity() {
        return Community.builder()
                .name(this.orgName)
                .description(this.orgDescription)
                .build();
    }
}
