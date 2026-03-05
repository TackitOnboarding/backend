package org.example.tackit.domain.executive.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.OrgStatus;
import org.example.tackit.domain.executive.dto.response.MemberListResDto;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExecutiveMemberService {

  private final MemberOrgRepository memberOrgRepository;
  private final MemberOrgValidator memberOrgValidator;

  // [ 모든 멤버 조회 ]
  public List<MemberListResDto> getMembers(Long requestMemberOrgId, String orgStatus) {
    // 1. 운영진 권한 체크
    MemberOrg requesterMemberOrg = memberOrgValidator.validateExecutive(requestMemberOrgId);
    Long orgId = requesterMemberOrg.getOrganization().getId();

    List<MemberOrg> memberOrgs;

    // 2. 조회
    if ("ALL".equalsIgnoreCase(orgStatus)) {
      memberOrgs = memberOrgRepository.findByOrganizationId(orgId);
    } else {
      OrgStatus status = OrgStatus.valueOf(orgStatus.toUpperCase());
      memberOrgs = memberOrgRepository.findByOrganizationIdAndOrgStatus(orgId, status);
    }

    return memberOrgs.stream()
        .map(mo -> MemberListResDto.builder()
            .memberOrgId(mo.getId())
            .nickname(mo.getNickname())
            .email(mo.getMember().getEmail())
            .orgStatus(mo.getOrgStatus().name())
            .createdAt(mo.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }

  // [ 멤버 승인 및 반려 ]
  @Transactional
  public void updateMemberApplicationStatus(Long requesterMemberOrgId, Long targetMemberOrgId,
      OrgStatus newStatus) {
    // 1. 운영진 권한 체크
    MemberOrg requester = memberOrgValidator.validateExecutive(requesterMemberOrgId);
    Long orgId = requester.getOrganization().getId();

    // 2. 타겟 대상 조회
    MemberOrg targetMember = memberOrgRepository.findById(targetMemberOrgId)
        .orElseThrow(() -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));

    // 3. 조직 일치 여부 검증
    if (!targetMember.getOrganization().getId().equals(orgId)) {
      throw new IllegalArgumentException("해당 조직의 가입 신청이 아닙니다.");
    }

    // 4. 상태 변경 (ACTIVE or REJECTED)
    targetMember.updateStatus(newStatus);
  }
}
