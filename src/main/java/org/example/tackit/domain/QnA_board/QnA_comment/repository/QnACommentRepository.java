package org.example.tackit.domain.QnA_board.QnA_comment.repository;

import org.example.tackit.domain.entity.MemberOrg;
import org.example.tackit.domain.entity.QnAComment;
import org.example.tackit.domain.entity.QnAPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnACommentRepository extends JpaRepository<QnAComment, Long> {
    List<QnAComment> findByQnAPost(QnAPost post);

    Page<QnAComment> findByWriter(MemberOrg writer, Pageable pageable);
}

