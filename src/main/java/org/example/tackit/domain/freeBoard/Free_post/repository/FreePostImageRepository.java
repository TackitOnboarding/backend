package org.example.tackit.domain.freeBoard.Free_post.repository;

import org.example.tackit.domain.entity.FreePostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreePostImageRepository extends JpaRepository<FreePostImage, Long> {
    List<FreePostImage> findByFreePostId(Long freePostId);
}
