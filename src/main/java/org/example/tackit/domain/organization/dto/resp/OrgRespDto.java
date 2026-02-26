package org.example.tackit.domain.organization.dto.resp;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.Org.OrgType;
import org.example.tackit.domain.entity.Org.Organization;

@Getter
@Builder
public class OrgRespDto {
    private Long orgId;
    private String orgName;
    private String orgType;
    private String universityName; // CLUB일 때만 값이 있고, COMMUNITY면 "연합/소모임" 등으로 표시
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
