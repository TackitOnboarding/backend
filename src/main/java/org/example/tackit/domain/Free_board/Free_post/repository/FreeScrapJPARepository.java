package org.example.tackit.domain.Free_board.Free_post.repository;

import org.example.tackit.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FreeScrapJPARepository extends JpaRepository<FreeScrap, Long> {
    // Page<FreeScrap> findByMemberAndType(Member member, Post type, Pageable pageable);

    // Optional<FreeScrap> findByMemberAndFreePost(Member member, FreePost freePost);

    boolean existsByFreePostIdAndMemberId(Long freePostId, Long memberId);

    Optional<FreeScrap> findByMemberAndFreePost(MemberOrg member, FreePost post);

}
