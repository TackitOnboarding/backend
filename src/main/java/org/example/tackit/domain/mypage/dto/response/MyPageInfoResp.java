package org.example.tackit.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.OrgType;
import org.example.tackit.domain.entity.org.Organization;

@Getter
@Builder
@AllArgsConstructor
public class MyPageInfoResp {
    private String nickname;
    private String email;
    private OrgType orgType;
    private String orgName;
    private String universityName;
    private String imageUrl;
    private MemberType memberType;
    private MemberRole memberRole;

    public static MyPageInfoResp from(MemberOrg memberOrg) {
        Organization organization = memberOrg.getOrganization();

        String universityName = (organization.getType() == OrgType.CLUB && organization.getUniversity() != null)
                ? organization.getUniversity().getUniversityName()
                : null;

        return MyPageInfoResp.builder()
                .nickname(memberOrg.getNickname())
                .email(memberOrg.getMember().getEmail())
                .orgType(organization.getType())
                .orgName(organization.getName())
                .universityName(universityName)
                .imageUrl(memberOrg.getProfileImageUrl())
                .memberType(memberOrg.getMemberType())
                .memberRole(memberOrg.getMemberRole())
                .build();
    }
}
