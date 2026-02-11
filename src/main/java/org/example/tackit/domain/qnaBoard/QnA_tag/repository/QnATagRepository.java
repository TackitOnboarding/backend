package org.example.tackit.domain.qnaBoard.QnA_tag.repository;

import org.example.tackit.domain.entity.QnATag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnATagRepository extends JpaRepository<QnATag, Long>, QnATagCustomRepository {
}
