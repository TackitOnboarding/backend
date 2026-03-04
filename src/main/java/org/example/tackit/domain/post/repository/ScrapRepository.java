package org.example.tackit.domain.post.repository;

import java.util.Optional;
import org.example.tackit.domain.entity.post.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  // 스크랩 여부 확인용
  boolean existsByMemberOrgIdAndPostId(Long memberOrgId, Long postId);

  // 삭제 시 조회용
  Optional<Scrap> findByMemberOrgIdAndPostId(Long memberOrgId, Long postId);
}