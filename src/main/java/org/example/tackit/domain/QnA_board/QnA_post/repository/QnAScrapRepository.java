package org.example.tackit.domain.QnA_board.QnA_post.repository;

import org.example.tackit.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QnAScrapRepository extends JpaRepository<QnAScrap, Long> {
    /*
    @EntityGraph(attributePaths = {"qnaPost", "qnaPost.writer"}) // 페이징 함께 지원, writer까지 fetch해서 n+1방지
    Page<QnAScrap> findByMemberAndQnaPost_Status(Member member, AccountStatus accountStatus, Pageable pageable);
     */

    boolean existsByQnaPostIdAndMemberId(Long qnaPostId, Long memberId);
}
