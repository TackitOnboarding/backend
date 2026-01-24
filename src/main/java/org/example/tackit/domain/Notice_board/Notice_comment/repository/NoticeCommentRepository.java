package org.example.tackit.domain.Notice_board.Notice_comment.repository;

import org.example.tackit.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
    // 특정 게시글에 달린 모든 댓글 조회
    List<NoticeComment> findByNoticePost(NoticePost post);

    Page<NoticeComment> findByWriter(Member member, Pageable pageable);
}
