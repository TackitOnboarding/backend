package org.example.tackit.domain.QnA_board.QnA_post.repository;

import org.example.tackit.domain.entity.MemberOrg;
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
            "AND w.club.id = :clubId " +
            "ORDER BY q.viewCount DESC, q.scrapCount DESC")
    List<QnAPost> findTop3PopularByClub(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("clubId") Long clubId,
            Pageable pageable);

    @Query("SELECT q FROM QnAPost q JOIN q.writer w " +
            "WHERE q.accountStatus = :status AND q.createdAt BETWEEN :start AND :end " +
            "AND w.community.id = :communityId " + // 이 부분만 w.community.id로 변경
            "ORDER BY q.viewCount DESC, q.scrapCount DESC")
    List<QnAPost> findTop3PopularByCommunity(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("communityId") Long communityId,
            Pageable pageable);
    // List<QnAPost> findTop3ByStatusOrderByViewCountDescScrapCountDesc(AccountStatus accountStatus);

    // 통합 인기 3개
    /*
    List<QnAPost> findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
            AccountStatus accountStatus, LocalDateTime startOfWeek, LocalDateTime now);
     */
}
