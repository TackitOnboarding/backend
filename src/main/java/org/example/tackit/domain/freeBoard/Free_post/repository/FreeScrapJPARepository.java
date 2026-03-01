package org.example.tackit.domain.freeBoard.Free_post.repository;

import java.util.Optional;
import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.FreeScrap;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FreeScrapJPARepository extends JpaRepository<FreeScrap, Long> {
  // Page<FreeScrap> findByMemberAndType(Member member, Post type, Pageable pageable);

  // Optional<FreeScrap> findByMemberAndFreePost(Member member, FreePost freePost);

  boolean existsByFreePostIdAndMemberId(Long freePostId, Long memberId);

  Optional<FreeScrap> findByMemberAndFreePost(MemberOrg member, FreePost post);

}
