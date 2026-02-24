package org.example.tackit.domain.executive.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.example.tackit.domain.member.repository.MemberOrgRepository;
import org.example.tackit.domain.executive.dto.response.MemberListResDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutiveMemberService {
    private final MemberOrgRepository memberOrgRepository;

    // 공통 권한 검증 : 요청자가 해당 조직의 멤버인지, 운영진인지 확인
    private void validateExecutiveRole(Long orgId, String email) {
        MemberOrg requester = memberOrgRepository.findByOrganizationIdAndMemberEmail(orgId, email)
                .orElseThrow(() -> new IllegalArgumentException("해당 조직의 멤버가 아닙니다."));

        if (requester.getMemberRole() != MemberRole.EXECUTIVE) {
            throw new AccessDeniedException("운영진 권한이 없습니다.");
        }

        if (requester.getOrgStatus() != OrgStatus.ACTIVE) {
            throw new IllegalStateException("활성화되지 않은 운영진 계정입니다.");
        }
    }

    // [ 모든 멤버 조회 ]
    public List<MemberListResDto> getMembers(Long orgId, String email, String orgStatus) {
        // 1. 운영진 권한 체크
        validateExecutiveRole(orgId, email);

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

    // [ 멤버 승인 ]
    public void approveMember(Long orgId, String email, Long memberOrgId) {
        // 1. 운영진 권한 체크
        validateExecutiveRole(orgId, email);

        // 2. 승인 대상 조회 및 상태 변경
        MemberOrg targetMember = memberOrgRepository.findById(memberOrgId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));

        targetMember.updateStatus(OrgStatus.ACTIVE);
    }

    // [ 멤버 반려 ]
    public void rejectMember(Long orgId, String email, Long memberOrgId) {
        // 1. 운영진 권한 체크
        validateExecutiveRole(orgId, email);

        // 2. 반려 대상 조회 및 상태 변경
        MemberOrg targetMember = memberOrgRepository.findById(memberOrgId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));

        if (!targetMember.getOrganization().getId().equals(orgId)) {
            throw new IllegalArgumentException("해당 조직의 가입 신청이 아닙니다.");
        }

        targetMember.updateStatus(OrgStatus.REJECTED);
    }
}
