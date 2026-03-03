package org.example.tackit.domain.memberOrg.repository;

import java.util.List;
import java.util.Optional;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberOrgRepository extends JpaRepository<MemberOrg, Long> {

  Optional<MemberOrg> findByOrganizationIdAndMemberEmail(Long orgId, String email);

  // 특정 소속(Org) 내에서 닉네임 중복 확인
  boolean existsByOrganizationIdAndNickname(Long orgId, String nickname);

  // 해당 소속(Org)에 가입된 사람이 있는지 확인 (최초 가입자 판별용)
  boolean existsByOrganizationId(Long orgId);

  @Query("SELECT mo FROM MemberOrg mo WHERE mo.member.id = :memberId AND mo.id = :profileId")
  Optional<MemberOrg> findByMemberIdAndProfileId(@Param("memberId") Long memberId,
      @Param("profileId") Long profileId);

  // 멀티 프로필 : 사용자의 이메일로 가입된 모든 프로필 조회
  List<MemberOrg> findAllByMemberEmail(String email);

  // 사용자의 ID로 가입된 모든 프로필 조회
  List<MemberOrg> findAllByMemberId(Long memberId);

  // 이메일과 프로필(MemberOrg) ID로 특정 소속 정보 조회
  Optional<MemberOrg> findByMemberEmailAndId(String email, Long id);

  Optional<MemberOrg> findByMemberIdAndId(Long memberId, Long profileId);

  // 특정 유저가 해당 프로필의 소유자인지 확인
  boolean existsByMemberIdAndId(Long memberId, Long id);

  // N+1 문제 방지를 위한 페치 조인 (성능 최적화)
  @Query("SELECT mo FROM MemberOrg mo JOIN FETCH mo.member WHERE mo.id = :id")
  Optional<MemberOrg> findByIdWithMember(@Param("id") Long id);

  // 조직 ID와 멤버 ID로 가입 여부 확인 (중복 가입 방지용)
  boolean existsByMemberIdAndOrganizationId(Long memberId, Long orgId);

  Optional<MemberOrg> findByMemberIdAndOrganizationId(Long memberId, Long orgId);

  boolean existsByMemberIdAndOrganizationIdAndOrgStatus(Long memberId, Long orgId,
      OrgStatus status);

  Optional<MemberOrg> findByMemberIdAndOrganizationIdAndOrgStatus(Long memberId, Long orgId,
      OrgStatus status);

  int countByOrganizationIdAndOrgStatus(Long orgId, OrgStatus orgStatus);

  List<MemberOrg> findByOrganizationId(Long orgId);   // 특정 조직의 모든 멤버 관계 조회

  List<MemberOrg> findByOrganizationIdAndOrgStatus(Long orgId,
      OrgStatus orgStatus);  // 특정 조직 + 특정 상태(PENDING, ACTIVE 등)의 멤버 관계 조회
}