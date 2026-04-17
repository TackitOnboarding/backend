package org.example.tackit.domain.member.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberProfileResp {
    private Long memberOrgId;
    private Long orgId;

    private String orgType;
    private String orgName;
    private String universityName;
    private String orgStatus;

    private String nickname;
    private String imageUrl;
    private String memberType;
    private String memberRole;

}
