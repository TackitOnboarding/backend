package org.example.tackit.domain.post.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.Organization;
import org.example.tackit.domain.entity.post.Comment;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.entity.post.PostCategory;
import org.example.tackit.domain.entity.post.PostType;
import org.example.tackit.domain.entity.post.Scrap;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;
import org.example.tackit.domain.post.dto.CategoryResponse;
import org.example.tackit.domain.post.dto.CommentResDto;
import org.example.tackit.domain.post.dto.PostCreateReqDto;
import org.example.tackit.domain.post.dto.PostDetailResDto;
import org.example.tackit.domain.post.dto.PostDetailResDto.PostInfo;
import org.example.tackit.domain.post.dto.PostPagingResDto;
import org.example.tackit.domain.post.dto.PostPagingResDto.PageInfo;
import org.example.tackit.domain.post.dto.PostSummaryResDto;
import org.example.tackit.domain.post.dto.PostUpdateReqDto;
import org.example.tackit.domain.post.repository.CommentRepository;
import org.example.tackit.domain.post.repository.PostRepository;
import org.example.tackit.domain.post.repository.ScrapRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final MemberOrgValidator memberOrgValidator;
  private final ScrapRepository scrapRepository;

  /**
   * 게시글 목록 조회
   */
  public PostPagingResDto getPostList(Long memberOrgId, PostType type, PostCategory category,
      Pageable pageable) {
    MemberOrg memberOrg = memberOrgValidator.validateActiveMembership(memberOrgId);
    Long orgId = memberOrg.getOrganization().getId();

    // 카테고리 유무에 따라 쿼리 분기
    Page<Post> postPage = (category != null)
        ? postRepository.findAllByOrganizationIdAndPostTypeAndCategoryAndActiveStatus(
        orgId, type, category, ActiveStatus.ACTIVE, pageable)
        : postRepository.findAllByOrganizationIdAndPostTypeAndActiveStatus(
            orgId, type, ActiveStatus.ACTIVE, pageable);

    // Entity -> DTO 변환
    List<PostSummaryResDto> postDtos = postPage.getContent().stream()
        .map(this::toSummaryResDto)
        .toList();

    // PageInfo 생성
    PageInfo pageInfo = PageInfo.builder()
        .currentPage(postPage.getNumber())
        .size(postPage.getSize())
        .totalElements(postPage.getTotalElements())
        .totalPages(postPage.getTotalPages())
        .isFirst(postPage.isFirst())
        .isLast(postPage.isLast())
        .build();

    return PostPagingResDto.builder()
        .posts(postDtos)
        .pageInfo(pageInfo)
        .build();
  }

  /**
   * 게시글 상세 조회
   */
  @Transactional
  public PostDetailResDto getPostDetail(Long memberOrgId, Long postId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);

    Post post = postRepository.findByIdWithWriter(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    // 조회수 증가
    post.increaseViewCnt();

    // 부가 정보 확인
    boolean isMine = post.getWriter().getId().equals(memberOrgId);
    boolean isScrap = scrapRepository.existsByMemberOrgIdAndPostId(memberOrgId, postId);

    // 댓글 계층 구조 조립
    List<Comment> comments = commentRepository.findAllByPostId(postId);
    List<CommentResDto> commentDtos = convertToCommentHierarchy(comments, memberOrgId);

    // PostInfo DTO 생성
    PostInfo postInfo = PostInfo.builder()
        .id(post.getId())
        .title(post.getTitle())
        .writer(convertToSimpleProfile(post.getWriter(), post.isAnonymous()))
        .content(post.getContent())
        .isAnonymous(post.isAnonymous())
        .isMine(isMine)
        .isScrap(isScrap)
        .viewCount(post.getViewCnt())
        .createdAt(post.getCreatedAt())
        .build();

    return PostDetailResDto.builder()
        .post(postInfo)
        .totalCommentCount(comments.size())
        .comments(commentDtos)
        .build();
  }

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
   * 게시글 스크랩
   */
  @Transactional
  public void scrapPost(Long memberOrgId, Long postId) {
    MemberOrg memberOrg = memberOrgValidator.validateActiveMembership(memberOrgId);
    Post post = findPostById(postId);

    if (scrapRepository.existsByMemberOrgIdAndPostId(memberOrgId, postId)) {
      throw new IllegalArgumentException("이미 스크랩한 게시글입니다.");
    }

    Scrap scrap = Scrap.builder()
        .memberOrg(memberOrg)
        .post(post)
        .build();

    scrapRepository.save(scrap);
  }

  /**
   * 게시글 스크랩 취소
   */
  @Transactional
  public void unscrapPost(Long memberOrgId, Long postId) {
    memberOrgValidator.validateActiveMembership(memberOrgId);
    findPostById(postId);

    Scrap scrap = scrapRepository.findByMemberOrgIdAndPostId(memberOrgId, postId)
        .orElseThrow(() -> new IllegalArgumentException("스크랩하지 않은 게시글입니다."));

    scrapRepository.delete(scrap);
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

  // 목록 요약 DTO 변환
  private PostSummaryResDto toSummaryResDto(Post post) {
    // 본문 요약 (100자 제한)
    String summary = post.getContent().length() > 100
        ? post.getContent().substring(0, 100) + "..."
        : post.getContent();

    // TODO 썸네일 이미지 관련 작업 필요
    String thumbnail = null;

    return PostSummaryResDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .contentSummary(summary)
        .postCategory(CategoryResponse.from(post.getCategory()))
        .thumbnail(thumbnail)
        .writer(convertToSimpleProfile(post.getWriter(), post.isAnonymous()))
        .createdAt(post.getCreatedAt())
        .isAnonymous(post.isAnonymous())
        .viewCount(post.getViewCnt())
        .build();
  }

  // 게시글/댓글 작성자 프로필 변환 및 익명 처리
  private SimpleMemberProfileDto convertToSimpleProfile(MemberOrg writer, boolean isAnonymous) {
    if (isAnonymous) {
      return SimpleMemberProfileDto.builder()
          .nickname("익명")
          .profileImageUrl(null)
          .memberRole(null)
          .memberType(null)
          .activityYear(null)
          .build();
    }
    return SimpleMemberProfileDto.builder()
        .orgMemberId(writer.getId())
        .nickname(writer.getNickname())
        .profileImageUrl(writer.getProfileImageUrl())
        .memberRole(MemberRole.valueOf(writer.getMemberRole().name()))
        .build();
  }

  // 댓글 계층 구조 변환 (List -> Tree)
  private List<CommentResDto> convertToCommentHierarchy(List<Comment> comments,
      Long currentUserId) {
    Map<Long, CommentResDto> map = new HashMap<>();
    List<CommentResDto> roots = new ArrayList<>();

    // 1. 모든 댓글을 DTO로 변환하여 Map에 저장
    for (Comment c : comments) {
      boolean isDeleted = c.getActiveStatus() == ActiveStatus.DELETED;
      boolean isMine = c.getWriter() != null && c.getWriter().getId().equals(currentUserId);

      // 삭제된 댓글 처리 (내용 마스킹)
      String content = isDeleted ? "삭제된 댓글입니다." : c.getContent();
      SimpleMemberProfileDto writerDto =
          isDeleted ? null : convertToSimpleProfile(c.getWriter(), false);

      CommentResDto dto = CommentResDto.builder()
          .id(c.getId())
          .writer(writerDto)
          .content(content)
          .isMine(isMine)
          .isDeleted(isDeleted)
          .createdAt(c.getCreatedAt())
          .children(new ArrayList<>()) // 자식 리스트 초기화
          .build();

      map.put(dto.getId(), dto);
    }

    // 2. 부모-자식 연결
    for (Comment c : comments) {
      CommentResDto childDto = map.get(c.getId());
      if (c.getParent() == null) {
        roots.add(childDto);
      } else {
        CommentResDto parentDto = map.get(c.getParent().getId());
        if (parentDto != null) {
          parentDto.getChildren().add(childDto);
        }
      }
    }
    return roots;
  }
}