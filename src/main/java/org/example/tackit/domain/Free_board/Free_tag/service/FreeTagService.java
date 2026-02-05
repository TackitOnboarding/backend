package org.example.tackit.domain.Free_board.Free_tag.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_tag.dto.response.FreeTagPostResponseDto;
import org.example.tackit.domain.Free_board.Free_tag.dto.response.FreeTagResponseDto;
import org.example.tackit.domain.Free_board.Free_tag.repository.FreeTagRepository;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FreeTagService {
    private final FreeTagRepository freeTagRepository;

    // 모든 태그 목록 가져오기
    public List<FreeTagResponseDto> getAllTags() {
        return freeTagRepository.findAll().stream()
                .map(tag -> FreeTagResponseDto.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .build())
                .toList();
    }

    // 태그별 게시물 불러오기
    /*
    @Transactional(readOnly = true)
    public PageResponseDTO<FreeTagPostResponseDto> getFreePostsByTag(Long tagId, Long orgId, Pageable pageable){
        Page<FreeTagPostResponseDto> page = freeTagRepository.findPostsByTagId(tagId, organization, pageable);
        return PageResponseDTO.from(page, Function.identity());
    }

     */

}
