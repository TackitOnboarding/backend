package org.example.tackit.domain.executive.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.example.tackit.domain.member.repository.MemberOrgRepository;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.executive.dto.response.MemberListResponse;
import org.example.tackit.domain.executive.repository.ExecutiveMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutiveMemberService {
    private final ExecutiveMemberRepository executiveMemberRepository;
    private final MemberRepository memberRepository;
    private final MemberOrgRepository memberOrgRepository;

    // [ 모든 멤버 조회 ]
    public List<MemberListResponse> getMembers(Long orgId, String orgStatus) {
        List<MemberOrg> memberOrgs;

        // 상태 조건 없다면 -> 전체
        if( orgStatus == null || orgStatus.isBlank() )
            memberOrgs = memberOrgRepository.findByOrganizationId(orgId);

        // 특정 상태(대기, 이용 중, 탈퇴) 조회
        else {
            OrgStatus status = OrgStatus.valueOf(orgStatus.toUpperCase());
            memberOrgs = memberOrgRepository.findByOrganizationIdAndOrgStatus(orgId, status);
        }

        return memberOrgs.stream()
                .map( mo -> MemberListResponse.builder()
                        .memberOrgId(mo.getId())
                        .nickname(mo.getNickname())
                        .email(mo.getMember().getEmail())
                        .orgStatus(mo.getOrgStatus().name())
                        .createdAt(mo.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

    }

    // [ 멤버 승인 ]
    public void approveMember(Long memberOrgId) {
        MemberOrg memberOrg = memberOrgRepository.findById(memberOrgId)
                .orElseThrow( () -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));

        // 상태를 ACTIVE로 변경
        memberOrg.updateStatus(OrgStatus.ACTIVE);
    }

    // [ 멤버 반려 ]
    public void rejectMember(Long memberOrgId) {
        MemberOrg memberOrg = memberOrgRepository.findById(memberOrgId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));

        // 상태를 REJECTED로 변경
        memberOrg.updateStatus(OrgStatus.REJECTED);
    }
}
