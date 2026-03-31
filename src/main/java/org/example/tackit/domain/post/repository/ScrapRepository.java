package org.example.tackit.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.example.tackit.domain.entity.post.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  // 스크랩 여부 확인용
  boolean existsByMemberOrgIdAndPostId(Long memberOrgId, Long postId);

  // 삭제 시 조회용
  Optional<Scrap> findByMemberOrgIdAndPostId(Long memberOrgId, Long postId);

  // 마이페이지 ) 스크랩한 글 조회
  @Query("select s from Scrap s " +
          "join fetch s.post p " +
          "join fetch p.writer w " +
          "where s.memberOrg.id = :memberOrgId " +
          "order by s.createdAt desc")
  List<Scrap> findAllByMemberOrgIdWithPost(@Param("memberOrgId") Long memberOrgId);
}