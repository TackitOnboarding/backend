package org.example.tackit.domain.auth.login.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiProfileDto {
    // 소속 정보
    private Long memberOrgId;
    private String orgName;
    private String orgType;

    // 소속에서 사용하는 개인 정보
    private String nickname;
    private String profileImage;
    private String memberType;
    private String memberRole;
}
