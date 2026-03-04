package org.example.tackit.domain.organization.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.example.tackit.domain.entity.Org.OrgType;
import org.example.tackit.domain.entity.Org.Organization;
import org.example.tackit.domain.entity.Org.University;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.organization.dto.req.OrgCreateReqDto;
import org.example.tackit.domain.organization.dto.req.OrgJoinReqDto;
import org.example.tackit.domain.organization.dto.resp.OrgRespDto;
import org.example.tackit.domain.organization.repository.OrganizationRepository;
import org.example.tackit.domain.university.repository.UniversityRepository;
import org.example.tackit.global.exception.CustomBaseException;
import org.example.tackit.global.exception.ErrorCode;
import org.example.tackit.global.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrganizationService {

  private final MemberRepository memberRepository;
  private final MemberOrgRepository memberOrgRepository;
  private final OrganizationRepository organizationRepository;
  private final UniversityRepository universityRepository;

  // [ 모임 생성 ]
  @Transactional
  public OrgRespDto createOrg(OrgCreateReqDto dto, String email) {
    // 1. 사용자 확인
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    // 2. 타입별 중복 체크 및 대학 조회
    University university = null;
    if (dto.getOrgType() == OrgType.CLUB) {
      university = universityRepository.findById(dto.getUniversityId())
          .orElseThrow(() -> new CustomBaseException(ErrorCode.UNIVERSITY_NOT_FOUND));

      // 같은 학교 내 동일 이름 동아리 중복 체크
      if (organizationRepository.existsByNameAndUniversityAndType(dto.getOrgName(), university,
          OrgType.CLUB)) {
        throw new CustomBaseException(ErrorCode.DUPLICATE_ORGANIZATION);
      }
    } else {
      // 전체 서비스 내 동일 이름 소모임 중복 체크
      if (organizationRepository.existsByNameAndType(dto.getOrgName(), OrgType.COMMUNITY)) {
        throw new CustomBaseException(ErrorCode.DUPLICATE_ORGANIZATION);
      }
    }

    // 3. 엔티티 생성 및 저장
    Organization organization = dto.toEntity(university);
    Organization savedOrg = organizationRepository.save(organization);

    // 4. 생성 성공 응답 반환
    return OrgRespDto.of(savedOrg);
  }

  // [ 모임 검색 ]
  @Transactional(readOnly = true)
  public List<OrgRespDto> searchOrgsByTypeAndName(OrgType orgType, String orgName) {
    List<Organization> orgs = organizationRepository.findByTypeAndNameContaining(orgType, orgName);

    return orgs.stream()
            .map(OrgRespDto::of)
            .toList();
  }

  // [ 모임 참여 ]
  @Transactional
  public void joinOrg(Long orgId, String email, OrgJoinReqDto dto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    // 조직 존재 유무 검토
    Organization organization = organizationRepository.findById(orgId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 모임입니다. ID: " + orgId));

    // 같은 모임 내 닉네임 중복 검토
    if (memberOrgRepository.existsByOrganizationIdAndNickname(orgId, dto.getNickname())) {
      throw new IllegalStateException("해당 모임에서 이미 사용 중인 닉네임입니다: " + dto.getNickname());
    }

    // 최초 가입자 여부 확인
    boolean isFirstMember = !memberOrgRepository.existsByOrganizationId(orgId);

    // 최초 가입자 && 운영진 -> ACTIVE 되도록
    OrgStatus status = OrgStatus.PENDING;
    if (isFirstMember && dto.getMemberRole() == MemberRole.EXECUTIVE) {
      status = OrgStatus.ACTIVE;
    }

    MemberOrg memberOrg = MemberOrg.builder()
        .member(member)
        .organization(organization) // 통합된 필드 사용
        .orgType(organization.getType())
        .nickname(dto.getNickname())
        .memberRole(dto.getMemberRole())
        .memberType(dto.getMemberType())
        .joinedYear(LocalDate.now().getYear())
        .orgStatus(status)
        .build();

    memberOrgRepository.save(memberOrg);
  }
}
