package org.example.tackit.domain.Notice_board.repository;

import org.example.tackit.domain.entity.NoticePostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticePostImageRepository extends JpaRepository<NoticePostImage, Long> {
    List<NoticePostImage> findByNoticePostId(Long noticePostId);
}
