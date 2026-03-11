package org.example.tackit.domain.memberOrg.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.OrgStatus;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberOrgService {

  private final MemberOrgRepository memberOrgRepository;
  private final MemberOrgValidator memberOrgValidator;

  // 특정 조직 소속 멤버 조회
  public Page<SimpleMemberProfileDto> getOrgMembers(Long orgId, Long memberOrgId, Pageable pageable) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    Page<MemberOrg> members = memberOrgRepository.findByOrganizationIdAndOrgStatus(
        orgId,
        OrgStatus.ACTIVE, pageable
    );

    return members.map(SimpleMemberProfileDto::from);
  }
}
