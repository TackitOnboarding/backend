package org.example.tackit.domain.Organization.service;

import lombok.AllArgsConstructor;
import org.example.tackit.domain.Organization.dto.req.OrgCreateReqDto;
import org.example.tackit.domain.Organization.dto.req.OrgJoinReqDto;
import org.example.tackit.domain.Organization.repository.ClubRepository;
import org.example.tackit.domain.Organization.repository.CommunityRepository;
import org.example.tackit.domain.Organization.repository.SchoolRepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.global.exception.ErrorCode;
import org.example.tackit.global.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class OrganizationService {
    private final MemberRepository memberRepository;
    private final MemberOrgRepository memberOrgRepository;

    private final ClubRepository clubRepository;
    private final CommunityRepository communityRepository;
    private final SchoolRepository schoolRepository;

    // [ 모임 생성 ]
    @Transactional
    public void createOrg(OrgCreateReqDto dto, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 동아리 ) 학교 코드(이름) + 해당 동아리가 없으면 생성 가능
        if( dto.getOrgType() == OrgType.CLUB ) {
            School school = schoolRepository.findById(dto.getSchoolId())
                            .orElseThrow( () -> new RuntimeException("해당 학교가 등록되어 있지 않습니다."));

            clubRepository.findByNameAndSchool(dto.getOrgName(), school)
                    .ifPresent(c -> { throw new RuntimeException("해당 학교에 이미 동일한 이름의 동아리가 존재합니다."); });

            Club club = clubRepository.save(dto.toClub(school));
        }

        // 소모임 ) 동일한 이름이 없으면 생성 가능
        else {
            communityRepository.findByName(dto.getOrgName())
                    .ifPresent(c -> { throw new RuntimeException("이미 동일한 이름의 소모임이 존재합니다."); });

            Community community = communityRepository.save(dto.toCommunity());
        }
    }

    // [ 모임 참여 ]
    @Transactional
    public void joinOrg(Long orgId, OrgType type, String email, OrgJoinReqDto dto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        MemberOrg.MemberOrgBuilder builder = MemberOrg.builder()
                .member(member)
                .orgType(type)
                .nickname(dto.getNickname())
                .memberRole(dto.getMemberRole())
                .memberType(dto.getMemberType())
                .joinedYear(LocalDate.now().getYear());

        boolean isFirstMember;

        // 소모임 존재 유무 검토
        if (type == OrgType.CLUB) {
            Club club = clubRepository.findById(orgId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 동아리입니다. ID: " + orgId));
            builder.club(club);
            isFirstMember = !memberOrgRepository.existsByClubId(orgId);
        } else {
            Community community = communityRepository.findById(orgId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 소모임입니다. ID: " + orgId));
            builder.community(community);
            isFirstMember = !memberOrgRepository.existsByCommunityId(orgId);
        }

        // 같은 모임 내 닉네임 중복 검토
        if (type == OrgType.CLUB) {
            if (memberOrgRepository.existsByClubIdAndNickname(orgId, dto.getNickname())) {
                throw new IllegalStateException("해당 동아리에서 이미 사용 중인 닉네임입니다: " + dto.getNickname());
            }
        } else {
            if (memberOrgRepository.existsByCommunityIdAndNickname(orgId, dto.getNickname())) {
                throw new IllegalStateException("해당 소모임에서 이미 사용 중인 닉네임입니다: " + dto.getNickname());
            }
        }

        // 최초 가입자라면 즉시 승인
        if (isFirstMember) {
            builder.orgStatus(OrgStatus.ACTIVE);
        } else {
            builder.orgStatus(OrgStatus.PENDING);
        }

        memberOrgRepository.save(builder.build());
    }
}
