package org.example.tackit.domain.freeBoard.Free_post.repository;

import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.FreeReport;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreePostReportRepository extends JpaRepository<FreeReport, Long> {
  // boolean existsByMemberAndFreePost(Member member, FreePost freePost);

  boolean existsByMemberAndFreePost(MemberOrg member, FreePost post);
}
