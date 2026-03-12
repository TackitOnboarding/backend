package org.example.tackit.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.domain.mypage.dto.response.MypageInfoResp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final MemberRepository memberRepository;
  private final MemberOrgRepository memberOrgRepository;
  private final MemberOrgValidator memberOrgValidator;

  // 내 정보 조회(닉네임, 조직(동아리라면 대학), 이메일)
  @Transactional(readOnly = true)
  public MypageInfoResp getMypageInfo(Long memberOrgId) {
    MemberOrg memberOrg = memberOrgValidator.validateActiveMembership(memberOrgId);

    return MypageInfoResp.from(memberOrg);
  }

    /*
    // 스크랩한 자유 게시글 조회
    @Transactional
    public PageResponseDTO<FreeScrapResponse> getScrapListByMember(String email, Pageable pageable) {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<FreeScrap> page = freeScrapJPARepository.findByMemberAndType(member, Post.Free, pageable);


        return PageResponseDTO.from(page, scrap -> {
            FreePost post = scrap.getFreePost();

            List<String> tags = freePostTagMapRepository.findByFreePost(post).stream()
                    .map(mapping -> mapping.getTag().getTagName())
                    .toList();

            return FreeScrapResponse.from(scrap, tags);
        });
    }

    // 내가 쓴 자유 게시글 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<FreeMyPostResponseDto> getMyPosts(String email, Pageable pageable) {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<FreePost> page = freePostJPARepository.findByWriterAndStatus(member, AccountStatus.ACTIVE, pageable);

        return PageResponseDTO.from(page, post -> {
            List<String> tags = freePostTagMapRepository.findByFreePost(post).stream()
                    .map(mapping -> mapping.getTag().getTagName())
                    .toList();

            return FreeMyPostResponseDto.from(post, tags);
        });
    }

    // 내가 쓴 댓글 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<FreeMyCommentResponseDto> getMyComments(String email, Pageable pageable) {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<FreeComment> comments = freeCommentRepository.findByWriter(member, pageable);

        return PageResponseDTO.from(comments, comment -> FreeMyCommentResponseDto.builder()
                .commentId(comment.getId())
                .postId(comment.getFreePost().getId())
                .writer(comment.getWriter().getNickname())
                .profileImageUrl(comment.getWriter().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .type(comment.getFreePost().getType())
                .build()
        );
    }

     */
}
