package org.example.tackit.domain.post.repository;

import java.util.Optional;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.entity.post.PostCategory;
import org.example.tackit.domain.entity.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

  // 카테고리 필터링된 목록 조회
  Page<Post> findAllByOrganizationIdAndPostTypeAndCategoryAndActiveStatus(
      Long organizationId,
      PostType postType,
      PostCategory category,
      ActiveStatus activeStatus,
      Pageable pageable
  );

  // 전체 목록 조회
  Page<Post> findAllByOrganizationIdAndPostTypeAndActiveStatus(
      Long organizationId,
      PostType postType,
      ActiveStatus activeStatus,
      Pageable pageable
  );

  // 게시글 상세 조회
  @Query("SELECT p FROM Post p JOIN FETCH p.writer w " +
      "WHERE p.id = :postId AND p.activeStatus = 'ACTIVE'")
  Optional<Post> findByIdWithWriter(@Param("postId") Long postId);

  // 관리자 신고 게시글 조회
  Page<Post> findAllByActiveStatusAndReportCntGreaterThanEqual(ActiveStatus activeStatus,
      int reportCount, Pageable pageable);
}