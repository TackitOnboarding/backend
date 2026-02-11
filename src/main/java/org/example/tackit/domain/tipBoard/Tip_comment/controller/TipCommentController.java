package org.example.tackit.domain.tipBoard.Tip_comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.tipBoard.Tip_comment.dto.req.TipCommentCreateDto;
import org.example.tackit.domain.tipBoard.Tip_comment.dto.req.TipCommentUpdateDto;
import org.example.tackit.domain.tipBoard.Tip_comment.dto.resp.TipCommentResponseDto;
import org.example.tackit.domain.tipBoard.Tip_comment.service.TipCommentService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tip-comments")
public class TipCommentController {

    private final TipCommentService tipCommentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<TipCommentResponseDto> createComment(
            @RequestBody TipCommentCreateDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ActiveProfile ProfileContext profile) {
        TipCommentResponseDto response = tipCommentService.createComment(request, userDetails.getUsername(), profile.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<TipCommentResponseDto>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ActiveProfile ProfileContext profile) {
        return ResponseEntity.ok(tipCommentService.getCommentByPost(postId, profile.id()));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<TipCommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody TipCommentUpdateDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ActiveProfile ProfileContext profile) {
        TipCommentResponseDto updateResponse = tipCommentService.updateComment(commentId, request, userDetails.getUsername(), profile.id());
        return ResponseEntity.ok().body(updateResponse);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ActiveProfile ProfileContext profile) {
        tipCommentService.deleteComment(commentId, userDetails.getUsername(), profile.id());
        return ResponseEntity.noContent().build();
    }

    // 댓글 신고
    @PostMapping("/{commentId}/report")
    public ResponseEntity<String> reportComment(
            @PathVariable long commentId,
            @ActiveProfile ProfileContext profile) {
        tipCommentService.increaseCommentReportCount(commentId, profile.id());
        return ResponseEntity.ok("댓글을 신고하였습니다.");
    }
}


