package org.example.tackit.domain.auth.login.repository;

import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.MemberOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberOrgRepository extends JpaRepository<MemberOrg, Long> {
    // 이메일과 프로필 ID로 소속 정보 조회
    Optional<MemberOrg> findByMemberEmailAndId(String email, Long id);

    // 특정 유저가 이 프로필의 주인인지 확인
    boolean existsByMemberIdAndId(Long memberId, Long id);

    List<MemberOrg> findAllByMemberId(Long memberId);

    // 알림이나 게시글 조회 시 Member 정보까지 한 번에 가져오기
    // 추후 N+1 문제를 방지하기 위해 @Query와 join fetch를 사용할 것
    @Query("SELECT mo FROM MemberOrg mo JOIN FETCH mo.member WHERE mo.id = :id")
    Optional<MemberOrg> findByIdWithMember(@Param("id") Long id);

    Long member(Member member);

    List<MemberOrg> findAllByMemberEmail(String email);

    boolean existsByClubId(Long clubId);
    boolean existsByCommunityId(Long communityId);

    // 닉네임 중복 체크용
    boolean existsByClubIdAndNickname(Long clubId, String nickname);
    boolean existsByCommunityIdAndNickname(Long communityId, String nickname);
}

