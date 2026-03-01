package org.example.tackit.domain.tipBoard.Tip_post.repository;

import java.util.Optional;
import org.example.tackit.domain.entity.TipPost;
import org.example.tackit.domain.entity.TipScrap;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TipScrapRepository extends JpaRepository<TipScrap, Long> {

  Optional<TipScrap> findByMemberOrgAndTipPost(MemberOrg memberOrg, TipPost tipPost);

  boolean existsByTipPostIdAndMemberOrg_Id(Long tipPostId, Long memberOrgId);


}
