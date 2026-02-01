package org.example.tackit.domain.Free_board.Free_post.repository;

import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.AccountStatus;
import org.example.tackit.domain.entity.QnAPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface FreePostJPARepository extends JpaRepository<FreePost, Long> {

    Page<FreePost> findByWriterIdAndAccountStatus(Long writerId, AccountStatus accountStatus, Pageable pageable);

    List<FreePost> findTop3ByWriterIdAndAccountStatusOrderByViewCountDescScrapCountDesc(Long writerId, AccountStatus accountStatus);


    // 인기 3개
    @Query("SELECT f FROM FreePost f JOIN f.writer w " +
            "WHERE f.accountStatus = :status AND f.createdAt BETWEEN :start AND :end " +
            "AND w.club.id = :clubId " +
            "ORDER BY f.viewCount DESC, f.scrapCount DESC")
    List<FreePost> findTop3PopularByClub(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("clubId") Long clubId,
            Pageable pageable);

    @Query("SELECT f FROM FreePost f JOIN f.writer w " +
            "WHERE f.accountStatus = :status AND f.createdAt BETWEEN :start AND :end " +
            "AND w.community.id = :communityId " + // 이 부분만 w.community.id로 변경
            "ORDER BY f.viewCount DESC, f.scrapCount DESC")
    List<FreePost> findTop3PopularByCommunity(
            @Param("status") AccountStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("communityId") Long communityId,
            Pageable pageable);

    // 인기 3개
    // List<FreePost> findTop3ByStatusOrderByViewCountDescScrapCountDesc(AccountStatus accountStatus);

    // 통합 인기 3개
    /*
    List<FreePost> findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
            AccountStatus accountStatus, LocalDateTime startOfWeek, LocalDateTime now);
     */
}
