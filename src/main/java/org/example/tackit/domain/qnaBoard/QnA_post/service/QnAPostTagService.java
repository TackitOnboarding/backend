package org.example.tackit.domain.qnaBoard.QnA_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.qnaBoard.QnA_tag.repository.QnAPostTagMapRepository;
import org.example.tackit.domain.qnaBoard.QnA_tag.repository.QnATagRepository;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.QnATag;
import org.example.tackit.domain.entity.QnATagMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnAPostTagService {
    private final QnATagRepository qnATagRepository;
    private final QnAPostTagMapRepository qnAPostTagMapRepository;

    // 태그 매핑 저장
    public List<String> assignTagsToPost(QnAPost post, List<Long> tagIds) {
        List<String> tagNames = new ArrayList<>();

        for (Long tagId : tagIds) {
            QnATag tag = qnATagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));
            qnAPostTagMapRepository.save(QnATagMap.builder()
                    .qnaPost(post)
                    .tag(tag)
                    .build());
            tagNames.add(tag.getTagName());
        }

        return tagNames;
    }

    // 게시글에 연결된 태그 모두 삭제
    public void deleteTagsByPost(QnAPost post) {
        qnAPostTagMapRepository.deleteAllByQnaPost(post);
    }

    // 게시글에 연결된 태그 이름 조회 - 단일 포스트
    // 단순 쿼리여도 1번이라 N+1 안생김
    public List<String> getTagNamesByPost(QnAPost post) {
        return qnAPostTagMapRepository.findByQnaPost(post).stream()
                .map(mapping -> mapping.getTag().getTagName())
                .toList();
    }

    // 게시글에 연결된 태그 이름 조회 - 다중 포스트
    // 게시글들 리스트로 한번에 받아서 매핑 (게시글 id - 태그 이름 리스트)
    public Map<Long, List<String>> getTagNamesByPosts(List<QnAPost> posts) {
        return qnAPostTagMapRepository.findByQnaPostIn(posts).stream() // 게시글 ID에 대한 태그 매핑 한번에 조회
                .collect(Collectors.groupingBy(     //게시글 ID 기준으로 태그 이름들 그룹핑
                        mapping -> mapping.getQnaPost().getId(),
                        Collectors.mapping(mapping -> mapping.getTag().getTagName(), Collectors.toList())
                ));
    }



}
