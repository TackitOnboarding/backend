package org.example.tackit.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.Org.MemberOrg;

@Getter
@Builder
public class SimpleMemberProfileDto {

  private Long orgMemberId;
  private String nickname;
  private String profileImageUrl;

  public static SimpleMemberProfileDto from(MemberOrg memberOrg) {
    if (memberOrg == null) {
      return null;
    }
    return SimpleMemberProfileDto.builder()
        .orgMemberId(memberOrg.getId())
        .nickname(memberOrg.getNickname())
        .profileImageUrl(memberOrg.getProfileImageUrl())
        .build();
  }
}