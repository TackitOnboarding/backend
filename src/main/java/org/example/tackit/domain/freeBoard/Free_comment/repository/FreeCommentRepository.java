package org.example.tackit.domain.freeBoard.Free_comment.repository;

import org.example.tackit.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeCommentRepository extends JpaRepository<FreeComment, Long> {
    // 특정 게시글에 달린 모든 댓글 조회
    List<FreeComment> findByFreePost(FreePost post);

    Page<FreeComment> findByWriter(Member member, Pageable pageable);
}
