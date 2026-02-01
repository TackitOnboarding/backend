package org.example.tackit.domain.QnA_board.QnA_comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.QnA_board.QnA_comment.dto.request.QnACommentCreateDto;
import org.example.tackit.domain.QnA_board.QnA_comment.dto.request.QnACommentUpdateDto;
import org.example.tackit.domain.QnA_board.QnA_comment.dto.response.QnACommentResponseDto;
import org.example.tackit.domain.QnA_board.QnA_comment.service.QnACommentService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna-comment")
public class QnACommentController {

    private final QnACommentService qnACommentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<QnACommentResponseDto> createComment(
            @RequestBody QnACommentCreateDto request,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        QnACommentResponseDto response = qnACommentService.createComment(request, user.getEmail(), profile.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<QnACommentResponseDto>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        return ResponseEntity.ok(qnACommentService.getCommentByPost(postId, profile.id()));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<QnACommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody QnACommentUpdateDto request,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        QnACommentResponseDto updateResponse = qnACommentService.updateComment(commentId, request, user.getEmail(), profile.id());
        return ResponseEntity.ok().body(updateResponse);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        qnACommentService.deleteComment(commentId, user.getEmail(), profile.id());
        return ResponseEntity.noContent().build();
    }

    // 댓글 신고
    @PostMapping("{commentId}/report")
    public ResponseEntity<String> reportComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        qnACommentService.increaseCommentReportCount(commentId, profile.id());
        return ResponseEntity.ok("댓글을 신고하였습니다.");
    }

}
