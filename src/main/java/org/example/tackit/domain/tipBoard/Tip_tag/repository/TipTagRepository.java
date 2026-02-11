package org.example.tackit.domain.tipBoard.Tip_tag.repository;

import org.example.tackit.domain.entity.TipTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipTagRepository extends JpaRepository<TipTag, Long>, TipTagCustomRepository {
}
