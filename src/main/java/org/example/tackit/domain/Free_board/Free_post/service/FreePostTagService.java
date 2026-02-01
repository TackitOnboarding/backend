package org.example.tackit.domain.Free_board.Free_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_tag.repository.FreePostTagMapRepository;
import org.example.tackit.domain.Free_board.Free_tag.repository.FreeTagRepository;
import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.FreeTag;
import org.example.tackit.domain.entity.FreeTagMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreePostTagService {
    private final FreeTagRepository freeTagRepository;
    private final FreePostTagMapRepository freePostTagMapRepository;

    // 태그 매핑 저장
    public List<String> assignTagsToPost(FreePost post, List<Long> tagIds) {
        List<String> tagNames = new ArrayList<>();

        for(Long tagId : tagIds) {
            FreeTag tag = freeTagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));
            freePostTagMapRepository.save(FreeTagMap.builder()
                    .freePost(post)
                    .tag(tag)
                    .build());
            tagNames.add(tag.getTagName());
        }
        return tagNames;
    }

    // 게시글에 연결된 모든 태그 삭제
    public void deleteTagsByPost(FreePost post) { freePostTagMapRepository.deleteAllByFreePost(post); }

    // 게시글에 연결된 태그 이름 조회
    public List<String> getTagNamesByPost(FreePost post) {
        return freePostTagMapRepository.findByFreePost(post).stream()
                .map(mapping -> mapping.getTag().getTagName())
                .toList();
    }

}
