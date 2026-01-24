package org.example.tackit.domain.admin.repository;

import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT u FROM Member u WHERE u.email <> 'admin' ORDER BY " +
            "CASE WHEN u.status = 0 THEN 0 ELSE 1 END")
    List<Member> findAllOrderByStatus();

    Optional<Member> findByEmail(String email);

    // 총 가입자 수
    @Query("SELECT COUNT(m) FROM Member m WHERE m.email <> 'admin'")
    Long countAll();

    // 이번 달/주 가입자 수
    @Query("SELECT COUNT(m) FROM Member m WHERE m.createdAt >= :date AND m.email <> 'admin'")
    Long countJoinedAfter(@Param("date")LocalDateTime date);

    // 탈퇴 회원 통계
    List<Member> findByStatus(Status status);

    // 1년마다 뉴비 -> 시니어 자동 갱신
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Member m " +
            "SET m.memberType = :newType " +  // memberType 필드 업데이트
            "WHERE m.memberType = :oldType " + // 기존 memberType 조건
            "AND m.joinedYear <= :thresholdYear")
    int bulkUpdateType(@Param("oldType") MemberType oldType,
                       @Param("newType") MemberType newType,
                       @Param("thresholdYear") int thresholdYear);
    // 닉네임 중복확인
    boolean existsByNickname(String nickname);

}
