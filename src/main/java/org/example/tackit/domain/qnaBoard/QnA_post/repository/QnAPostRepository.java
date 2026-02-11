package org.example.tackit.domain.qnaBoard.QnA_post.repository;

import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QnAPostRepository extends JpaRepository<QnAPost, Long> {

    Page<QnAPost> findByWriterAndAccountStatus(MemberOrg writer, AccountStatus status, Pageable pageable);
    Page<QnAPost> findByWriterIdAndAccountStatus(Long orgId, AccountStatus status, Pageable pageable);

    List<QnAPost> findTop3ByWriterIdAndAccountStatusOrderByViewCountDescScrapCountDesc(Long orgId, AccountStatus status);


    // 인기 3개
    @Query("SELECT q FROM QnAPost q JOIN q.writer w " +
            "WHERE q.accountStatus = :status AND q.createdAt BETWEEN :start AND :end " +
            "AND w.organization.id = :orgId " + // w(MemberOrg) 내의 organization 필드 참조
            "ORDER BY q.viewCount DESC, q.scrapCount DESC")
    List<QnAPost> findTop3PopularByOrg(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("orgId") Long orgId,
            Pageable pageable);
    // List<QnAPost> findTop3ByStatusOrderByViewCountDescScrapCountDesc(AccountStatus accountStatus);

    // 통합 인기 3개
    /*
    List<QnAPost> findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
            AccountStatus accountStatus, LocalDateTime startOfWeek, LocalDateTime now);
     */
}
