package org.example.tackit.domain.qnaBoard.QnA_comment.repository;

import java.util.List;
import org.example.tackit.domain.entity.QnAComment;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnACommentRepository extends JpaRepository<QnAComment, Long> {

  List<QnAComment> findByQnAPost(QnAPost post);

  Page<QnAComment> findByWriter(MemberOrg writer, Pageable pageable);
}

