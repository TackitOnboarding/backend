package org.example.tackit.domain.Tip_board.Tip_post.repository;

import org.example.tackit.domain.entity.AccountStatus;
import org.example.tackit.domain.entity.TipPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TipPostRepository extends JpaRepository<TipPost, Long> {

    Page<TipPost> findByWriterId(Long orgId, Pageable pageable);

    // 인기 3개
    @Query("SELECT t FROM TipPost t JOIN t.writer w " +
            "WHERE t.accountStatus = :status AND t.createdAt BETWEEN :start AND :end " +
            "AND w.club.id = :clubId " +
            "ORDER BY t.viewCount DESC, t.scrapCount DESC")
    List<TipPost> findTop3PopularByClub(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("clubId") Long clubId,
            Pageable pageable);

    @Query("SELECT t FROM TipPost t JOIN t.writer w " +
            "WHERE t.accountStatus = :status AND t.createdAt BETWEEN :start AND :end " +
            "AND w.community.id = :communityId " + // 이 부분만 w.community.id로 변경
            "ORDER BY t.viewCount DESC, t.scrapCount DESC")
    List<TipPost> findTop3PopularByCommunity(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("communityId") Long communityId,
            Pageable pageable);

    List<TipPost> findTop3ByWriterIdOrderByViewCountDescScrapCountDesc(Long orgId);
    // 인기 3개
    // List<TipPost> findTop3ByStatusOrderByViewCountDescScrapCountDesc(AccountStatus accountStatus);

    // 통합 인기 3개
    /*
    List<TipPost> findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
            AccountStatus accountStatus, LocalDateTime startOfWeek, LocalDateTime now);
     */
}
