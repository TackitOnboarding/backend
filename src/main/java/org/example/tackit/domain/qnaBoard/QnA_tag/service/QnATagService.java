package org.example.tackit.domain.qnaBoard.QnA_tag.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.qnaBoard.QnA_tag.dto.response.QnATagPostResponseDto;
import org.example.tackit.domain.qnaBoard.QnA_tag.dto.response.QnATagResponseDto;
import org.example.tackit.domain.qnaBoard.QnA_tag.repository.QnATagRepository;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class QnATagService {
    private final QnATagRepository qnATagRepository;

    // 모든 태그 목록 가져오기
    public List<QnATagResponseDto> getAllTags() {
        return qnATagRepository.findAll().stream()
                .map(tag -> QnATagResponseDto.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .build())
                .toList();
    }

    // 태그별 게시물 불러오기
    @Transactional(readOnly = true)
    public PageResponseDTO<QnATagPostResponseDto> getPostsByTag(Long tagId, String organization, Pageable pageable) {
        Page<QnATagPostResponseDto> page = qnATagRepository.findPostsByTagId(tagId, organization, pageable);
        return PageResponseDTO.from(page, Function.identity()); // 이미 DTO라 변환이 필요 없음
    }

}
