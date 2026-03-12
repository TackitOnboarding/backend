package org.example.tackit.domain.organization.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.MemberType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgJoinReqDto {
    private String nickname;
    private MemberRole memberRole;
    private MemberType memberType;
}
