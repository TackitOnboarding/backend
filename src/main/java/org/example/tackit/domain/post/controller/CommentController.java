package org.example.tackit.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.post.dto.CommentCreateReqDto;
import org.example.tackit.domain.post.dto.CommentIdResDto;
import org.example.tackit.domain.post.dto.CommentUpdateReqDto;
import org.example.tackit.domain.post.service.CommentService;
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
@RequestMapping("/api") // 공통 루트 경로 설정
public class CommentController {

  private final CommentService commentService;

  // 댓글 작성
  // 경로: /api/posts/{postId}/comments
  @PostMapping("/posts/{postId}/comments")
  public ResponseEntity<ApiResponse<CommentIdResDto>> createComment(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long postId,
      @Valid @RequestBody CommentCreateReqDto reqDto
  ) {
    Long commentId = commentService.createComment(profileContext.id(), postId, reqDto);
    return ApiResponse.success(HttpStatus.CREATED, "댓글 작성 성공", CommentIdResDto.from(commentId));
  }

  // 댓글 수정
  // 경로: /api/comments/{commentId}
  @PatchMapping("/comments/{commentId}")
  public ResponseEntity<ApiResponse<CommentIdResDto>> updateComment(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long commentId,
      @Valid @RequestBody CommentUpdateReqDto reqDto
  ) {
    commentService.updateComment(profileContext.id(), commentId, reqDto);
    return ApiResponse.success(HttpStatus.OK, "댓글 수정 성공", CommentIdResDto.from(commentId));
  }

  // 댓글 삭제
  // 경로: /api/comments/{commentId}
  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<ApiResponse<Object>> deleteComment(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long commentId
  ) {
    commentService.deleteComment(profileContext.id(), commentId);
    return ApiResponse.success(HttpStatus.OK, "댓글 삭제 성공");
  }
}