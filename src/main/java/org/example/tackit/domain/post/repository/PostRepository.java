package org.example.tackit.domain.post.repository;

import java.util.Optional;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

  @Query("SELECT p FROM Post p JOIN FETCH p.writer WHERE p.id = :id AND p.activeStatus = :status")
  Optional<Post> findByIdAndActiveStatus(@Param("id") Long id,
      @Param("status") ActiveStatus status);
}