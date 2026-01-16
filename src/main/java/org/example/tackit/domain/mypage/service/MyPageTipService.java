package org.example.tackit.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Tip_board.Tip_comment.repository.TipCommentRepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipMemberRepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostRepository;
import org.example.tackit.domain.Tip_board.Tip_tag.repository.TipPostTagMapRepository;
import org.example.tackit.domain.admin.repository.MemberRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.mypage.dto.response.TipMyCommentResponseDto;
import org.example.tackit.domain.mypage.dto.response.TipMyPostResponseDto;
import org.example.tackit.domain.mypage.dto.response.TipScrapResponse;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipScrapRepository;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageTipService {
    private final TipScrapRepository tipScrapRepository;
    private final MemberRepository memberRepository;
    private final TipPostRepository tipPostRepository;
    private final TipPostTagMapRepository tipPostTagMapRepository;
    private final TipMemberRepository tipMemberRepository;
    private final TipCommentRepository tipCommentRepository;

    // 스크랩한 tip 게시글 조회
    @Transactional
    public PageResponseDTO<TipScrapResponse> getScrapListByMember(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<TipScrap> page = tipScrapRepository.findByMemberAndType(member, Post.Tip, pageable);

        return PageResponseDTO.from(page, scrap -> {
            TipPost post = scrap.getTipPost();

            List<String> tags = tipPostTagMapRepository.findByTipPost(post).stream()
                    .map(mapping -> mapping.getTag().getTagName())
                    .toList();

            return TipScrapResponse.from(scrap, tags);
        });
    }

    // 내가 쓴 tip 게시글 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<TipMyPostResponseDto> getMyPosts(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<TipPost> page = tipPostRepository.findByWriterAndStatus(member, Status.ACTIVE, pageable);

        return PageResponseDTO.from(page, post -> {
            List<String> tags = tipPostTagMapRepository.findByTipPost(post).stream()
                    .map(mapping -> mapping.getTag().getTagName())
                    .toList();

            return TipMyPostResponseDto.from(post, tags);
        });

    }

    // 내가 쓴 tip 댓글 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<TipMyCommentResponseDto> getMyComments(String email, Pageable pageable) {
        Member member = tipMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<TipComment> commentPage = tipCommentRepository.findByWriter(member, pageable);

        return PageResponseDTO.from(commentPage, TipMyCommentResponseDto::fromEntity);
    }

}
