package org.example.tackit.domain.tipBoard.Tip_post.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.PageResponseDTO;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.Notification;
import org.example.tackit.domain.entity.NotificationType;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Post;
import org.example.tackit.domain.entity.TipPost;
import org.example.tackit.domain.entity.TipPostImage;
import org.example.tackit.domain.entity.TipReport;
import org.example.tackit.domain.entity.TipScrap;
import org.example.tackit.domain.member.repository.MemberOrgRepository;
import org.example.tackit.domain.notification.service.NotificationService;
import org.example.tackit.domain.tipBoard.Tip_post.dto.request.TipPostReqDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.request.TipPostUpdateDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.response.TipPopularPostRespDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.response.TipPostRespDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.response.TipScrapRespDto;
import org.example.tackit.domain.tipBoard.Tip_post.repository.TipPostReportRepository;
import org.example.tackit.domain.tipBoard.Tip_post.repository.TipPostRepository;
import org.example.tackit.domain.tipBoard.Tip_post.repository.TipScrapRepository;
import org.example.tackit.domain.tipBoard.Tip_tag.repository.TipPostTagMapRepository;
import org.example.tackit.global.exception.ErrorCode;
import org.example.tackit.global.exception.MemberNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TipPostService {

  private final TipPostRepository tipPostRepository;
  private final MemberOrgRepository memberOrgRepository;
  private final TipScrapRepository tipScrapRepository;
  private final TipPostReportRepository tipPostReportRepository;
  private final TipPostTagMapRepository tipPostTagMapRepository;
  private final TipTagService tagService;
  private final S3UploadService s3UploadService;
  private final NotificationService notificationService;

  public PageResponseDTO<TipPostRespDto> getActivePostsByOrganization(Long orgId,
      Pageable pageable) {
    Page<TipPost> page = tipPostRepository.findByWriterId(orgId, pageable);

    return PageResponseDTO.from(page, post -> {
      List<String> tags = tipPostTagMapRepository.findByTipPost(post).stream()
          .map(mapping -> mapping.getTag().getTagName())
          .toList();

      boolean anonymous = post.isAnonymous();

      return TipPostRespDto.builder()
          .id(post.getId())
          .writer(anonymous ? "익명" : post.getWriter().getNickname())
          .profileImageUrl(anonymous ? null : post.getWriter().getProfileImageUrl())
          .title(post.getTitle())
          .content(post.getContent())
          .createdAt(post.getCreatedAt())
          .tags(tags)
          .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
          .isAnonymous(anonymous)
          .build();
    });
  }

  // [ 게시글 상세 조회 ]
  @Transactional
  public TipPostRespDto getPostById(Long id, Long orgId, Long memberId) {
    TipPost tipPost = tipPostRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

    // 현재 접속한 소속(orgId)의 글이 맞는지 체크
    if (!tipPost.getWriter().getId().equals(orgId)) {
      throw new AccessDeniedException("해당 조직의 게시글이 아닙니다.");
    }

        /*
        if (!tipPost.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비활성화된 게시글입니다.");
        }

         */

    tipPost.increaseViewCount();

    List<String> tagNames = tagService.getTagNamesByPost(tipPost);

    // 스크랩 여부 조회
    boolean isScrap = tipScrapRepository.existsByTipPostIdAndMemberOrg_Id(id, memberId);

    // 익명 여부 조회
    boolean anonymous = tipPost.isAnonymous();

    return TipPostRespDto.builder()
        .id(tipPost.getId())
        .writer(anonymous ? "익명" : tipPost.getWriter().getNickname())
        .profileImageUrl(anonymous ? null : tipPost.getWriter().getProfileImageUrl())
        .title(tipPost.getTitle())
        .content(tipPost.getContent())
        .tags(tagNames)
        .imageUrl(tipPost.getImages().isEmpty() ? null : tipPost.getImages().get(0).getImageUrl())
        .createdAt(tipPost.getCreatedAt())
        .isScrap(isScrap)
        .isAnonymous(anonymous)
        .build();
  }

  // [ 게시글 작성 ] : 선배만 가능
  @Transactional
  public TipPostRespDto createPost(TipPostReqDto dto, String email, Long orgId, MultipartFile image)
      throws IOException {
    // 1. 유저 조회
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    if (member.getMemberType() != MemberType.SENIOR) {
      throw new AccessDeniedException("SENIOR만 게시글을 작성할 수 있습니다.");
    }

    // 2. 게시글 생성
    TipPost post = TipPost.builder()
        .writer(member)
        .title(dto.getTitle())
        .content(dto.getContent())
        .createdAt(LocalDateTime.now())
        .type(Post.Tip)
        .activeStatus(ActiveStatus.ACTIVE)
        .reportCount(0)
        .isAnonymous(dto.isAnonymous())
        .build();

    // 3. 이미지 업로드 & 연관관계 매핑 (단일 파일만)
    if (image != null && !image.isEmpty()) {
      String imageUrl = s3UploadService.saveFile(image);
      TipPostImage imageEntity = TipPostImage.builder()
              .imageUrl(imageUrl)
              .build();
      post.addImage(imageEntity); // 기존 이미지 clear 후 하나만 저장
    }

    tipPostRepository.save(post);

    List<String> tagNames = tagService.assignTagsToPost(post, dto.getTagIds());

    boolean anonymous = post.isAnonymous();

    // 응답 DTO 구성 (imageUrl 하나만)
    return TipPostRespDto.builder()
            .id(post.getId())
            .writer(anonymous ? "익명" : member.getNickname())
            .profileImageUrl(anonymous ? null : member.getProfileImageUrl())
            .title(post.getTitle())
            .content(post.getContent())
            .createdAt(post.getCreatedAt())
            .tags(tagNames)
            .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
            .isAnonymous(anonymous)
            .isScrap(false)
            .build();
    }


  // [ 게시글 수정 ] : 작성자만
  @Transactional
  public TipPostRespDto update(Long id, TipPostUpdateDto dto, String email, Long orgId,
      MultipartFile image) throws IOException {
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    TipPost post = tipPostRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    if (!post.getWriter().getId().equals(member.getId())) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }

    // 기본 정보 수정
    post.update(dto.getTitle(), dto.getContent());

    // 이미지 수정 로직
    String currentImageUrl =
        post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();

    // 1) 삭제 요청
    if (Boolean.TRUE.equals(dto.getRemoveImage())) {
      if (currentImageUrl != null) {
        s3UploadService.deleteImage(currentImageUrl);
      }
      post.clearImages();
    }

    // 2) 새 이미지 업로드 (교체 or 추가)
    if (image != null && !image.isEmpty()) {
      if (currentImageUrl != null) {
        s3UploadService.deleteImage(currentImageUrl);
        post.clearImages();
      }
      String newImageUrl = s3UploadService.saveFile(image);
      TipPostImage newImage = TipPostImage.builder()
          .imageUrl(newImageUrl)
          .build();
      post.addImage(newImage);
    }

    // 태그 다시 매핑
    tagService.deleteTagsByPost(post);
    List<String> tagNames = tagService.assignTagsToPost(post, dto.getTagIds());

    boolean anonymous = post.isAnonymous();

    return TipPostRespDto.builder()
        .id(post.getId())
        .writer(anonymous ? "익명" : post.getWriter().getNickname())
        .profileImageUrl(anonymous ? null : post.getWriter().getProfileImageUrl())
        .title(post.getTitle())
        .content(post.getContent())
        .createdAt(post.getCreatedAt())
        .tags(tagNames)
        .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
        .isAnonymous(anonymous)
        .build();
  }


  // [ 게시글 삭제 ]
  @Transactional
  public void deletePost(Long id, String email, Long orgId) {
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    TipPost post = tipPostRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

    // 권한 체크 : 요청 유저가 작성자인지
    if (!post.getWriter().getId().equals(member.getId())) {
      throw new AccessDeniedException("해당 게시글을 수정할 권한이 없습니다.");
    }
    post.delete();
  }

  // [ 게시글 스크랩 ]
  @Transactional
  public TipScrapRespDto toggleScrap(Long id, String email, Long orgId) {
    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    TipPost post = tipPostRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    if (!post.getWriter().getId().equals(orgId)) {
      throw new AccessDeniedException("해당 조직 게시글이 아닙니다.");
    }

    Optional<TipScrap> exisiting = tipScrapRepository.findByMemberOrgAndTipPost(member, post);

    if (exisiting.isPresent()) {
      tipScrapRepository.delete(exisiting.get());
      post.decreaseScrapCount();
      return new TipScrapRespDto(false, null);
    }
    TipScrap scrap = TipScrap.builder()
        .memberOrg(member)
        .tipPost(post)
        .savedAt(LocalDateTime.now())
        .build();

    tipScrapRepository.save(scrap);
    post.increaseScrapCount();

    // 알림 전송
    if (!post.getWriter().getId().equals(member.getId())) {

      // 알림 엔티티 생성
      Notification notification = Notification.builder()
          .member(post.getWriter().getMember())
          .memberOrgId(post.getWriter().getId())
          .type(NotificationType.SCRAP)
          .message(member.getNickname() + "님이 글을 스크랩했습니다.")
          .fromMemberOrgId(member.getId())
          .relatedUrl("/api/tip-posts/" + post.getId())
          .build();

      //3. 알림 저장 및 전송을 위해 NotificationService 호출
      notificationService.send(notification);
    }
    return new TipScrapRespDto(true, scrap.getSavedAt());
  }


  // [ 게시글 신고 ]
  @Transactional
  public String report(Long postId, String email, Long orgId) {
    TipPost post = tipPostRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

    MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
        .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

    boolean alreadyReported = tipPostReportRepository.existsByReporterAndTipPost(member, post);

    if (alreadyReported) {
      return "이미 신고한 게시글입니다.";
    }
    tipPostReportRepository.save(
        TipReport.builder()
            .reporter(member)
            .tipPost(post)
            .build()
    );
    // 신고 횟수 증가
    post.increaseReportCount();
    return "게시글을 신고하였습니다.";
  }

  // 인기 3개
  @Transactional
  public List<TipPopularPostRespDto> getPopularPosts(Long orgId) {
    List<TipPost> popularPosts = tipPostRepository.findTop3ByWriterIdOrderByViewCountDescScrapCountDesc(
        orgId);

    return popularPosts.stream()
        .map(TipPopularPostRespDto::from)
        .toList();

  }


}
