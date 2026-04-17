package org.example.tackit.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.member.dto.resp.MemberProfileResp;
import org.example.tackit.domain.member.dto.resp.MyInfoResp;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.global.exception.ErrorCode;
import org.example.tackit.global.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberOrgRepository memberOrgRepository;

    public MyInfoResp getMyInfo(String email) {
        // 계정 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<MemberOrg> memberOrgs = memberOrgRepository.findAllByMemberEmail(email);

        List<MemberProfileResp> profileList = memberOrgs.stream()
                .map(memberOrg -> {
                    MemberProfileResp.MemberProfileRespBuilder builder = MemberProfileResp.builder()
                            .memberOrgId(memberOrg.getId())
                            .orgId(memberOrg.getOrganization().getId())
                            .orgType(memberOrg.getOrgType().name())
                            .orgName(memberOrg.getOrganization().getName())
                            .nickname(memberOrg.getNickname())
                            .imageUrl(memberOrg.getProfileImageUrl())
                            .memberType(memberOrg.getMemberType().name())
                            .memberRole(memberOrg.getMemberRole().name())
                            .orgStatus(memberOrg.getOrgStatus().name());

                    // 동아리(CLUB)일 경우 대학교 이름 주입
                    if ("CLUB".equals(memberOrg.getOrgType().name())) {
                        if (memberOrg.getOrganization().getUniversity() != null) {
                            builder.universityName(memberOrg.getOrganization().getUniversity().getUniversityName());
                        }
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());

        return MyInfoResp.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .profiles(profileList)
                .build();
    }
}
