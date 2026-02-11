package org.example.tackit.domain.qnaBoard.QnA_tag.repository;

import org.example.tackit.domain.qnaBoard.QnA_tag.dto.response.QnATagPostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QnATagCustomRepository {
    Page<QnATagPostResponseDto> findPostsByTagId(Long tagId, String organization, Pageable pageable);
}
