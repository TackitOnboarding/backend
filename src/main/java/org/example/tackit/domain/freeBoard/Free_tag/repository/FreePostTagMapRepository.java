package org.example.tackit.domain.freeBoard.Free_tag.repository;

import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.FreeTag;
import org.example.tackit.domain.entity.FreeTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreePostTagMapRepository extends JpaRepository<FreeTagMap, Long> {
    void deleteAllByFreePost(FreePost freePost);
    List<FreeTagMap> findByFreePost(FreePost freePost);
    List<FreeTagMap> findByTag(FreeTag tag);
}
