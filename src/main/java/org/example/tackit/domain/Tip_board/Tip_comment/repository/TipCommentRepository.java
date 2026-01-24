package org.example.tackit.domain.Tip_board.Tip_comment.repository;

import org.example.tackit.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipCommentRepository extends JpaRepository<TipComment, Long> {
    List<TipComment> findByTipPost(TipPost post);
    Page<TipComment> findByWriter(Member writer, Pageable pageable);
}