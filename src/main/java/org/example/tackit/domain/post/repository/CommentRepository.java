package org.example.tackit.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.example.tackit.domain.entity.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("SELECT c FROM Comment c JOIN FETCH c.writer WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
  List<Comment> findAllByPostId(@Param("postId") Long postId);

  @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :commentId")
  Optional<Comment> findByIdWithPost(@Param("commentId") Long commentId);

  @Query("select c from Comment c " +
          "join fetch c.post p " +
          "where c.writer.id = :memberOrgId " +
          "order by c.createdAt desc")
  List<Comment> findAllByWriterIdWithPost(@Param("memberOrgId") Long memberOrgId);
}