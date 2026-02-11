package org.example.tackit.domain.noticeBoard.Notice_post.repository;

import org.example.tackit.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeScrapRepository extends JpaRepository<NoticeScrap, Long> {

    Optional<NoticeScrap> findByMemberAndNoticePost(Member member, NoticePost noticePost);
    @EntityGraph(attributePaths = {"noticePost", "noticePost.writer"}) // 페이징 함께 지원, writer까지 fetch해서 n+1방지
    Page<NoticeScrap> findByMember(Member member, Pageable pageable);
    boolean existsByNoticePostIdAndMemberId(Long noticePostId, Long memberId);
}
