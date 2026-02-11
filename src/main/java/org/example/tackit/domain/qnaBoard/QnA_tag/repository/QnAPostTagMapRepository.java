package org.example.tackit.domain.qnaBoard.QnA_tag.repository;

import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.QnATagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnAPostTagMapRepository extends JpaRepository<QnATagMap, Long> {
    // 게시글에 연결된 모든 태그 매핑 삭제
    void deleteAllByQnaPost(QnAPost qnaPost);
    // 게시글에 연결된 태그 매핑 조회
    List<QnATagMap> findByQnaPost(QnAPost qnaPost);
    // 특정 태그에 연결된 게시글 조회
    List<QnATagMap> findByQnaPostIn(List<QnAPost> posts);

}
