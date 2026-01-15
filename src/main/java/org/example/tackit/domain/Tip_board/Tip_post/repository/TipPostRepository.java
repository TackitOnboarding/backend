package org.example.tackit.domain.Tip_board.Tip_post.repository;


import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.Status;
import org.example.tackit.domain.entity.TipPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TipPostRepository extends JpaRepository<TipPost, Long> {

    Page<TipPost> findByOrganizationAndStatus(String organization, Status status, Pageable pageable);

    Page<TipPost> findByWriterAndStatus(Member writer, Status status, Pageable pageable );

    long countByStatus(Status status);

    // 인기 3개
    List<TipPost> findTop3ByStatusOrderByViewCountDescScrapCountDesc(Status status);

    // 통합 인기 3개
    List<TipPost> findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
            Status status, LocalDateTime startOfWeek, LocalDateTime now);
}
