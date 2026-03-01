package org.example.tackit.domain.qnaBoard.QnA_post.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QnAPostRepository extends JpaRepository<QnAPost, Long> {

  Page<QnAPost> findByWriterAndActiveStatus(MemberOrg writer, ActiveStatus status,
      Pageable pageable);

  Page<QnAPost> findByWriterIdAndActiveStatus(Long orgId, ActiveStatus status, Pageable pageable);

  List<QnAPost> findTop3ByWriterIdAndActiveStatusOrderByViewCountDescScrapCountDesc(Long orgId,
      ActiveStatus status);


  // 인기 3개
  @Query("SELECT q FROM QnAPost q JOIN q.writer w " +
      "WHERE q.activeStatus = :status AND q.createdAt BETWEEN :start AND :end " +
      "AND w.organization.id = :orgId " + // w(MemberOrg) 내의 organization 필드 참조
      "ORDER BY q.viewCount DESC, q.scrapCount DESC")
  List<QnAPost> findTop3PopularByOrg(
      @Param("status") ActiveStatus status,
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
