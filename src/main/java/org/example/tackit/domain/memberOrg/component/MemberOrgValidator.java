package org.example.tackit.domain.memberOrg.component;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberOrgValidator {

  private final MemberOrgRepository memberOrgRepository;

  // 활동 회원 체크 메서드
  public MemberOrg validateActiveMembership(Long orgId, Long memberOrgId) {
    MemberOrg memberOrg = memberOrgRepository.findById(memberOrgId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 조직 멤버 프로필입니다."));

    if (!memberOrg.getOrganization().getId().equals(orgId)) {
      throw new IllegalArgumentException("해당 조직의 프로필이 아닙니다.");
    }

    if (memberOrg.getOrgStatus() != OrgStatus.ACTIVE) {
      throw new IllegalArgumentException("해당 조직의 활동 중인 회원만 접근할 수 있습니다.");
    }
    return memberOrg;
  }

  public MemberOrg validateExecutive(Long orgId, Long memberOrgId) {
    MemberOrg memberOrg = validateActiveMembership(orgId, memberOrgId);

    if (memberOrg.getMemberRole() != MemberRole.EXECUTIVE) {
      throw new IllegalArgumentException("해당 조직의 운영진만 접근할 수 있습니다.");
    }
    return memberOrg;
  }
}