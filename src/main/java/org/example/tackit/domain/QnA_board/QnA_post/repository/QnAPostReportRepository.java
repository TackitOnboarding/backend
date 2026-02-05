package org.example.tackit.domain.QnA_board.QnA_post.repository;

import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.QnAReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnAPostReportRepository extends JpaRepository<QnAReport, Long> {
    boolean existsByMemberAndQnaPost(Member member, QnAPost qnaPost);

    boolean existsByMemberAndQnaPost(MemberOrg memberOrg, QnAPost qnAPost);
}
