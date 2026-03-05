package org.example.tackit.domain.memberOrg.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.org.MemberOrg;

@Getter
@Builder
public class SimpleMemberProfileDto implements Comparable<SimpleMemberProfileDto> {

  private Long orgMemberId;
  private String nickname;
  private String profileImageUrl;
  private MemberRole memberRole;
  private MemberType memberType;
  private int activityYear;

  public static SimpleMemberProfileDto from(MemberOrg memberOrg) {
    if (memberOrg == null) {
      return null;
    }
    return SimpleMemberProfileDto.builder()
        .orgMemberId(memberOrg.getId())
        .nickname(memberOrg.getNickname())
        .profileImageUrl(memberOrg.getProfileImageUrl())
        .memberRole(memberOrg.getMemberRole())
        .memberType(memberOrg.getMemberType())
        .activityYear(memberOrg.getActivityYear())
        .build();
  }

  @Override
  public int compareTo(SimpleMemberProfileDto other) {
    // 운영진 우선 정렬 
    int myRolePriority = (this.memberRole == MemberRole.EXECUTIVE) ? 0 : 1;
    int otherRolePriority = (other.memberRole == MemberRole.EXECUTIVE) ? 0 : 1;

    if (myRolePriority != otherRolePriority) {
      return Integer.compare(myRolePriority, otherRolePriority);
    }

    // 같은 역할 내에서는 닉네임 가나다순 비교
    return this.nickname.compareTo(other.nickname);
  }
}