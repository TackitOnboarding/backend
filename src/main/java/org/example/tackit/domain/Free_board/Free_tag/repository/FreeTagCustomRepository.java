package org.example.tackit.domain.Free_board.Free_tag.repository;

import org.example.tackit.domain.Free_board.Free_tag.dto.response.FreeTagPostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreeTagCustomRepository {
    Page<FreeTagPostResponseDto> findPostsByTagId(Long tagId, String organization, Pageable pageable);
}
