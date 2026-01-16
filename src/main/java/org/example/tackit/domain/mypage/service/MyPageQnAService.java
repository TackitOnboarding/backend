package org.example.tackit.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.QnA_board.QnA_comment.repository.QnACommentRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAMemberRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.QnA_board.QnA_tag.repository.QnAPostTagMapRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.QnAComment;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.Status;
import org.example.tackit.domain.mypage.dto.response.QnAMyCommentResponseDto;
import org.example.tackit.domain.mypage.dto.response.QnAMyPostResponseDto;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageQnAService {
    private final QnAMemberRepository qnAMemberRepository;
    private final QnAPostRepository qnAPostRepository;
    private final QnACommentRepository qnACommentRepository;
    private final QnAPostTagMapRepository qnAPostTagMapRepository;

    // 내가 쓴 게시글 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<QnAMyPostResponseDto> getMyPosts(String email, Pageable pageable) {
        Member member = qnAMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<QnAPost> postPage = qnAPostRepository.findByWriterAndStatus(member, Status.ACTIVE, pageable);
        List<QnAPost> posts = postPage.getContent();

        // 태그 일괄 조회 (N+1 방지)
        Map<Long, List<String>> tagMap = qnAPostTagMapRepository.findByQnaPostIn(posts).stream()
                .collect(Collectors.groupingBy(
                        mapping -> mapping.getQnaPost().getId(),
                        Collectors.mapping(mapping -> mapping.getTag().getTagName(), Collectors.toList())
                ));

        return PageResponseDTO.from(postPage, post ->
                QnAMyPostResponseDto.fromEntity(post, tagMap.getOrDefault(post.getId(), List.of()))
        );
    }


    // 내가 쓴 댓글 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<QnAMyCommentResponseDto> getMyComments(String email, Pageable pageable) {
        Member member = qnAMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<QnAComment> commentPage = qnACommentRepository.findByWriter(member, pageable);

        return PageResponseDTO.from(commentPage, QnAMyCommentResponseDto::fromEntity);
    }



}
