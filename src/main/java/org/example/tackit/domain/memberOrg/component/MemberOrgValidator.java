package org.example.tackit.domain.memberOrg.component;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.OrgStatus;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.global.exception.CustomBaseException;
import org.example.tackit.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberOrgValidator {

  private final MemberOrgRepository memberOrgRepository;

  // 활동 회원 체크 메서드
  public MemberOrg validateActiveMembership(Long memberOrgId) {
    MemberOrg memberOrg = memberOrgRepository.findById(memberOrgId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 조직 멤버 프로필입니다."));

    if (memberOrg.getOrgStatus() != OrgStatus.ACTIVE) {
      throw new IllegalArgumentException("해당 조직의 활동 중인 회원만 접근할 수 있습니다.");
    }
    return memberOrg;
  }

  // 운영진 체크 메서드
  public MemberOrg validateExecutive(Long memberOrgId) {
    MemberOrg memberOrg = validateActiveMembership(memberOrgId);

    if (memberOrg.getMemberRole() != MemberRole.EXECUTIVE) {
      throw new IllegalArgumentException("해당 조직의 운영진만 접근할 수 있습니다.");
    }
    return memberOrg;
  }

  // 신입 회원 체크 메서드
  public MemberOrg validateNewbie(Long memberOrgId) {
    MemberOrg memberOrg = validateActiveMembership(memberOrgId);

    if (memberOrg.getMemberType() != MemberType.NEWBIE) {
      throw new IllegalArgumentException("신입 회원(NEWBIE)만 접근할 수 있습니다.");
    }
    return memberOrg;
  }

  // 선배 회원 체크 메서드
  public MemberOrg validateSenior(Long memberOrgId) {
    MemberOrg memberOrg = validateActiveMembership(memberOrgId);

    if (memberOrg.getMemberType() != MemberType.SENIOR) {
      throw new IllegalArgumentException("선배 회원(SENIOR)만 접근할 수 있습니다.");
    }
    return memberOrg;
  }

  // 소유자 검증 메서드
  public void validateOwner(MemberOrg memberOrg, String email) {
    if( !memberOrg.getMember().getEmail().equals(email)) {
      throw new CustomBaseException(ErrorCode.ACCESS_DENIED_PROFILE);
    }
  }
}