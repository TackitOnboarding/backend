package org.example.tackit.domain.admin.repository;

import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.MemberType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT u FROM Member u WHERE u.email <> 'admin' ORDER BY " +
            "CASE WHEN u.activeStatus = 0 THEN 0 ELSE 1 END")
    List<Member> findAllOrderByActiveStatus();

    Page<Member> findAllByActiveStatus(ActiveStatus status, Pageable pageable);

    Optional<Member> findByEmail(String email);

    // 총 가입자 수
    @Query("SELECT COUNT(m) FROM Member m WHERE m.email <> 'admin'")
    Long countAll();

    // 이번 달/주 가입자 수
    @Query("SELECT COUNT(m) FROM Member m WHERE m.createdAt >= :date AND m.email <> 'admin'")
    Long countJoinedAfter(@Param("date")LocalDateTime date);

    // 탈퇴 회원 통계
    List<Member> findByActiveStatus(ActiveStatus activeStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE MemberOrg mo " +
            "SET mo.memberType = :newType " +
            "WHERE mo.memberType = :oldType " +
            "AND mo.joinedYear <= :thresholdYear")
    int bulkUpdateMemberType(@Param("oldType") MemberType oldType,
                             @Param("newType") MemberType newType,
                             @Param("thresholdYear") int thresholdYear);

}
