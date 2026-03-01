package org.example.tackit.domain.tipBoard.Tip_comment.repository;

import java.util.List;
import org.example.tackit.domain.entity.TipComment;
import org.example.tackit.domain.entity.TipPost;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipCommentRepository extends JpaRepository<TipComment, Long> {

  List<TipComment> findByTipPost(TipPost post);

  Page<TipComment> findByWriter(MemberOrg writer, Pageable pageable);
}