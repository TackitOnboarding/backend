package org.example.tackit.domain.Tip_board.Tip_tag.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Tip_board.Tip_tag.dto.response.TipTagPostResponseDto;
import org.example.tackit.domain.Tip_board.Tip_tag.dto.response.TipTagResponseDto;
import org.example.tackit.domain.Tip_board.Tip_tag.repository.TipTagRepository;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TipTagService {
    private final TipTagRepository tipTagRepository;

    // 모든 태그 목록 가져오기
    public List<TipTagResponseDto> getAllTags() {
        return tipTagRepository.findAll().stream()
                .map(tag -> TipTagResponseDto.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .build())
                .toList();
    }

    // 태그별 게시물 불러오기
    /*
    @Transactional(readOnly = true)
    public PageResponseDTO<TipTagPostResponseDto> getPostsByTag(Long tagId, String organization, Pageable pageable) {
        Page<TipTagPostResponseDto> page = tipTagRepository.findPostsByTagId(tagId, organization, pageable);
        return PageResponseDTO.from(page, Function.identity()); // 이미 DTO라 변환이 필요 없음
    }

     */

}
