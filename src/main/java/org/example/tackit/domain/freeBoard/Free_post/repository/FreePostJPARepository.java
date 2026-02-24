package org.example.tackit.domain.freeBoard.Free_post.repository;

import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface FreePostJPARepository extends JpaRepository<FreePost, Long> {

    Page<FreePost> findByWriterIdAndActiveStatus(Long writerId, ActiveStatus activeStatus, Pageable pageable);

    Page<FreePost> findAllByOrganizationIdAndActiveStatus(Long orgId, ActiveStatus status, Pageable pageable);

    List<FreePost> findTop3ByOrganizationIdAndActiveStatusOrderByViewCountDescScrapCountDesc(Long orgId, ActiveStatus activeStatus);

    // 인기 3개
    @Query("SELECT f FROM FreePost f " +
            "WHERE f.activeStatus = :status " +
            "AND f.createdAt BETWEEN :start AND :end " +
            "AND f.writer.organization.id = :orgId " +
            "ORDER BY f.viewCount DESC, f.scrapCount DESC")
    List<FreePost> findTop3PopularByOrg(
            @Param("status") ActiveStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("orgId") Long orgId,
            Pageable pageable);

    // 인기 3개
    // List<FreePost> findTop3ByStatusOrderByViewCountDescScrapCountDesc(AccountStatus accountStatus);

    // 통합 인기 3개
    /*
    List<FreePost> findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
            AccountStatus accountStatus, LocalDateTime startOfWeek, LocalDateTime now);
     */
}
