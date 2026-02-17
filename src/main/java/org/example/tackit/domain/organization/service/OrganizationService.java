package org.example.tackit.domain.organization.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.example.tackit.domain.entity.Org.OrgType;
import org.example.tackit.domain.entity.Org.Organization;
import org.example.tackit.domain.entity.Org.School;
import org.example.tackit.domain.member.repository.MemberOrgRepository;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.organization.dto.req.OrgCreateReqDto;
import org.example.tackit.domain.organization.dto.req.OrgJoinReqDto;
import org.example.tackit.domain.organization.repository.OrganizationRepository;
import org.example.tackit.domain.organization.repository.SchoolRepository;
import org.example.tackit.global.exception.ErrorCode;
import org.example.tackit.global.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrganizationService {

  private final MemberRepository memberRepository;
  private final MemberOrgRepository memberOrgRepository;
  private final SchoolRepository schoolRepository;
  private final OrganizationRepository organizationRepository;

  // [ 모임 생성 ]
  @Transactional
  public void createOrg(OrgCreateReqDto dto, String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    Organization organization;

    // 타입별 체크
    if (dto.getOrgType() == OrgType.CLUB) {
      School school = schoolRepository.findById(dto.getSchoolId())
          .orElseThrow(() -> new RuntimeException("해당 학교가 등록되어 있지 않습니다."));

      // 같은 학교 내 동일한 이름의 동아리가 있는지 확인
      organizationRepository.findByNameAndSchoolAndType(dto.getOrgName(), school, OrgType.CLUB)
          .ifPresent(o -> {
            throw new RuntimeException("해당 학교에 이미 동일한 이름의 동아리가 존재합니다.");
          });

      // 엔티티 생성 및 저장 (학교 정보 포함)
      organization = dto.toEntity(school);
    } else {
      // 서비스 전체에서 동일한 이름의 소모임이 있는지 확인
      organizationRepository.findByNameAndType(dto.getOrgName(), OrgType.COMMUNITY)
          .ifPresent(o -> {
            throw new RuntimeException("이미 동일한 이름의 소모임이 존재합니다.");
          });

      // 엔티티 생성 및 저장 (학교 정보 null)
      organization = dto.toEntity(null);
    }

    organizationRepository.save(organization);
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

    MemberOrg memberOrg = MemberOrg.builder()
        .member(member)
        .organization(organization) // 통합된 필드 사용
        .orgType(organization.getType())
        .nickname(dto.getNickname())
        .memberRole(dto.getMemberRole())
        .memberType(dto.getMemberType())
        .joinedYear(LocalDate.now().getYear())
        .orgStatus(isFirstMember ? OrgStatus.ACTIVE : OrgStatus.PENDING) // 삼항 연산자로 깔끔하게
        .build();

    memberOrgRepository.save(memberOrg);
  }
}
