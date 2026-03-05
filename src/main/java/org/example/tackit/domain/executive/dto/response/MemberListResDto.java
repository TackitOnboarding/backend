package org.example.tackit.domain.executive.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.org.MemberOrg;

@Getter
@Builder
@AllArgsConstructor
public class MemberListResDto {

  private Long memberOrgId;
  private String nickname;
  private String email;
  private String orgStatus;
  private MemberRole memberRole;
  private MemberType memberType;
  private LocalDateTime createdAt;

  public static MemberListResDto from(MemberOrg memberOrg) {
    return MemberListResDto.builder()
        .memberOrgId(memberOrg.getId())
        .nickname(memberOrg.getNickname())
        .email(memberOrg.getMember().getEmail())
        .orgStatus(memberOrg.getOrgStatus().name())
        .memberRole(memberOrg.getMemberRole())
        .memberType(memberOrg.getMemberType())
        .createdAt(memberOrg.getCreatedAt())
        .build();
  }
}
