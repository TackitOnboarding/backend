package org.example.tackit.domain.tipBoard.Tip_post.repository;

import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TipScrapRepository extends JpaRepository<TipScrap, Long> {
    Optional<TipScrap> findByMemberOrgAndTipPost(MemberOrg memberOrg, TipPost tipPost);
    boolean existsByTipPostIdAndMemberOrg_Id(Long tipPostId, Long memberOrgId);


}
