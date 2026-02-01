package org.example.tackit.domain.Notice_board.Notice_post.repository;

import org.example.tackit.domain.entity.NoticePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticePostRepository extends JpaRepository<NoticePost, Long> {

    Page<NoticePost> findByWriterId(Long memberOrgId, Pageable pageable);

}
