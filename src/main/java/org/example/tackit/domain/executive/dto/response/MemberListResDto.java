package org.example.tackit.domain.executive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MemberListResDto {
    private Long memberOrgId;
    private String nickname;
    private String email;
    private String orgStatus;
    private LocalDateTime createdAt;
}
