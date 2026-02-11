package org.example.tackit.domain.tipBoard.Tip_post.repository;

import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.TipPost;
import org.example.tackit.domain.entity.TipReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipPostReportRepository extends JpaRepository<TipReport, Long> {
    boolean existsByReporterAndTipPost(MemberOrg reporter, TipPost tipPost);


}
