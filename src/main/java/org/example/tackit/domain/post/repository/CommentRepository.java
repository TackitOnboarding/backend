package org.example.tackit.domain.post.repository;

import java.util.List;
import org.example.tackit.domain.entity.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}