package org.example.tackit.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.Organization;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.entity.post.PostType;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.domain.post.dto.PostCreateReqDto;
import org.example.tackit.domain.post.dto.PostUpdateReqDto;
import org.example.tackit.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final MemberOrgRepository memberOrgRepository;
  private final MemberOrgValidator memberOrgValidator;

  /**
   * 게시글 작성
   */
  @Transactional
  public Long createPost(Long memberOrgId, PostCreateReqDto reqDto) {
    MemberOrg writer = validateWriterByPostType(memberOrgId, reqDto.getPostType());

    Organization organization = writer.getOrganization();

    Post post = Post.builder()
        .postType(reqDto.getPostType())
        .category(reqDto.getPostCategory())
        .writer(writer)
        .organization(organization)
        .title(reqDto.getTitle())
        .content(reqDto.getContent())
        .isAnonymous(reqDto.getIsAnonymous())
        .commentEnabled(reqDto.getCommentEnabled())
        .build();

    return postRepository.save(post).getId();
  }

  /**
   * 게시글 수정
   */
  @Transactional
  public void updatePost(Long memberOrgId, Long postId, PostUpdateReqDto reqDto) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    Post post = findPostById(postId);

    validatePostWriter(post, memberOrgId);

    post.update(
        reqDto.getTitle(),
        reqDto.getContent(),
        reqDto.getPostCategory(),
        reqDto.getIsAnonymous(),
        reqDto.getCommentEnabled()
    );
  }

  /**
   * 게시글 삭제
   */
  @Transactional
  public void deletePost(Long memberOrgId, Long postId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    Post post = findPostById(postId);

    validatePostWriter(post, memberOrgId);

    post.delete();
  }

  /**
   * PostType 별 작성 권한 검증
   */
  private MemberOrg validateWriterByPostType(Long memberOrgId, PostType postType) {
    return switch (postType) {
      case TIP ->
        // TIP은 SENIOR만 작성 가능
          memberOrgValidator.validateSenior(memberOrgId);
      case QNA ->
        // QNA는 NEWBIE만 작성 가능
          memberOrgValidator.validateNewbie(memberOrgId);
      default ->
        // TODO 나머지는 활동 회원(Active)이면 작성 가능, NOTICE와 ACTIVITY 작성권한 확인 필요
          memberOrgValidator.validateActiveMembership(memberOrgId);
    };
  }

  /**
   * 게시글 존재 여부 확인 및 반환
   */
  private Post findPostById(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
  }

  /**
   * 본인이 작성한 글인지 체크하는 메서드
   */
  private void validatePostWriter(Post post, Long memberOrgId) {
    if (!post.getWriter().getId().equals(memberOrgId)) {
      throw new IllegalArgumentException("해당 게시글 작성자만 접근 가능합니다.");
    }
  }
}