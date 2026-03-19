package org.example.tackit.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.service.AuthService;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.entity.post.Scrap;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.domain.mypage.dto.request.UpdateProfileReq;
import org.example.tackit.domain.mypage.dto.response.MyCommentListResp;
import org.example.tackit.domain.mypage.dto.response.MyPostListResp;
import org.example.tackit.domain.mypage.dto.response.MyPageInfoResp;
import org.example.tackit.domain.mypage.dto.response.MyScrapListResp;
import org.example.tackit.domain.post.repository.CommentRepository;
import org.example.tackit.domain.post.repository.PostRepository;
import org.example.tackit.domain.post.repository.ScrapRepository;
import org.example.tackit.global.exception.BusinessException;
import org.example.tackit.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

  private final MemberRepository memberRepository;
  private final MemberOrgRepository memberOrgRepository;
  private final MemberOrgValidator memberOrgValidator;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final ScrapRepository scrapRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;

  // 내 정보 조회(닉네임, 조직(동아리라면 대학), 이메일)
  public MyPageInfoResp getMypageInfo(Long memberOrgId) {
    MemberOrg memberOrg = memberOrgValidator.validateActiveMembership(memberOrgId);

    return MyPageInfoResp.from(memberOrg);
  }

  // 내 정보 수정
  public void updateProfile(Long memberOrgId, UpdateProfileReq request) {

    MemberOrg memberOrg = memberOrgValidator.validateActiveMembership(memberOrgId);
    Member member = memberOrg.getMember();

    // 같은 조직 내 닉네임 중복 방지
    if (request.getNickname() != null) {

      boolean exists = memberOrgRepository
              .existsByOrganizationIdAndNickname(
                      memberOrg.getOrganization().getId(),
                      request.getNickname()
              );

      if (exists) {
        throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
      }
      memberOrg.updateNickname(request.getNickname());
    }

    if (request.getPassword() != null) {
      member.updatePassword(passwordEncoder.encode(request.getPassword()));
    }

    if (request.getProfileImageUrl() != null) {
      memberOrg.updateProfileImage(request.getProfileImageUrl());
    }
  }

  // 작성한 글 조회
  public List<MyPostListResp> getMyPosts(Long memberOrgId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    List<Post> posts = postRepository.findAllByWriterIdWithDetails(memberOrgId);

    return posts.stream()
            .map(MyPostListResp::from)
            .toList();
  }

  // 작성한 댓글 조회
  public List<MyCommentListResp> getMyComments(Long memberOrgId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    return commentRepository.findAllByWriterIdWithPost(memberOrgId).stream()
            .map(MyCommentListResp::from)
            .toList();
  }

  // 스크랩한 게시글 조회
  public List<MyScrapListResp> getMyScraps(Long memberOrgId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    List<Scrap> scraps = scrapRepository.findAllByMemberOrgIdWithPost(memberOrgId);

    return scraps.stream()
            .map(MyScrapListResp::from)
            .toList();
  }

}
