package org.example.tackit.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.post.dto.PostCreateReqDto;
import org.example.tackit.domain.post.dto.PostIdResDto;
import org.example.tackit.domain.post.dto.PostUpdateReqDto;
import org.example.tackit.domain.post.service.PostService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;

  // 게시글 작성
  @PostMapping
  public ResponseEntity<ApiResponse<PostIdResDto>> createPost(
      @ActiveProfile ProfileContext profileContext,
      @Valid @RequestBody PostCreateReqDto reqDto
  ) {
    Long postId = postService.createPost(profileContext.id(), reqDto);
    return ApiResponse.success(HttpStatus.CREATED, "게시글 작성 성공", PostIdResDto.from(postId));
  }

  // 게시글 수정
  @PatchMapping("/{postId}")
  public ResponseEntity<ApiResponse<PostIdResDto>> updatePost(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long postId,
      @Valid @RequestBody PostUpdateReqDto reqDto
  ) {
    postService.updatePost(profileContext.id(), postId, reqDto);
    return ApiResponse.success(HttpStatus.OK, "게시글 수정 성공", PostIdResDto.from(postId));
  }

  // 게시글 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<ApiResponse<Object>> deletePost(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long postId
  ) {
    postService.deletePost(profileContext.id(), postId);
    return ApiResponse.success(HttpStatus.OK, "게시글 삭제 성공");
  }

  // 게시글 스크랩
  @PostMapping("/{postId}/scrap")
  public ResponseEntity<ApiResponse<Object>> scrapPost(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long postId
  ) {
    postService.scrapPost(profileContext.id(), postId);
    return ApiResponse.success(HttpStatus.OK, "게시글 스크랩 성공");
  }

  // 게시글 스크랩 취소
  @DeleteMapping("/{postId}/scrap")
  public ResponseEntity<ApiResponse<Object>> unscrapPost(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long postId
  ) {
    postService.unscrapPost(profileContext.id(), postId);
    return ApiResponse.success(HttpStatus.OK, "게시글 스크랩 취소 성공");
  }
}