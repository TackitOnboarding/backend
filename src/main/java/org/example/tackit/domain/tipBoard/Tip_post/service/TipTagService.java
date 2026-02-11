package org.example.tackit.domain.tipBoard.Tip_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.tipBoard.Tip_tag.repository.TipPostTagMapRepository;
import org.example.tackit.domain.tipBoard.Tip_tag.repository.TipTagRepository;
import org.example.tackit.domain.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("tipTagPostService")
@RequiredArgsConstructor
public class TipTagService {
    private final TipTagRepository tipTagRepository;
    private final TipPostTagMapRepository tipPostTagMapRepository;

    // 태그 매핑 저장
    public List<String> assignTagsToPost(TipPost post, List<Long> tagIds) {
        List<String> tagNames = new ArrayList<>();

        for (Long tagId : tagIds) {
            TipTag tag = tipTagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));
            tipPostTagMapRepository.save(TipTagMap.builder()
                    .tipPost(post)
                    .tag(tag)
                    .build());
            tagNames.add(tag.getTagName());
        }

        return tagNames;
    }

    // 게시글에 연결된 태그 모두 삭제
    public void deleteTagsByPost(TipPost post) {
        tipPostTagMapRepository.deleteAllByTipPost(post);
    }

    // 게시글에 연결된 태그 이름 조회 - 단일 포스트
    // 단순 쿼리여도 1번이라 N+1 안생김
    public List<String> getTagNamesByPost(TipPost post) {
        return tipPostTagMapRepository.findByTipPost(post).stream()
                .map(mapping -> mapping.getTag().getTagName())
                .toList();
    }

    // 게시글에 연결된 태그 이름 조회 - 다중 포스트
    // 게시글들 리스트로 한번에 받아서 매핑 (게시글 id - 태그 이름 리스트)
    public Map<Long, List<String>> getTagNamesByPosts(List<TipPost> posts) {
        return tipPostTagMapRepository.findByTipPostIn(posts).stream() // 게시글 ID에 대한 태그 매핑 한번에 조회
                .collect(Collectors.groupingBy(     //게시글 ID 기준으로 태그 이름들 그룹핑
                        mapping -> mapping.getTipPost().getId(),
                        Collectors.mapping(mapping -> mapping.getTag().getTagName(), Collectors.toList())
                ));
    }



}
