package org.example.tackit.domain.tipBoard.Tip_tag.repository;

import org.example.tackit.domain.entity.TipPost;
import org.example.tackit.domain.entity.TipTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipPostTagMapRepository extends JpaRepository<TipTagMap, Long> {
    // 게시글에 연결된 모든 태그 매핑 삭제
    void deleteAllByTipPost(TipPost tipPost);
    // 게시글에 연결된 태그 매핑 조회
    List<TipTagMap> findByTipPost(TipPost tipPost);
    // 특정 태그에 연결된 게시글 조회
    List<TipTagMap> findByTipPostIn(List<TipPost> posts);

}
