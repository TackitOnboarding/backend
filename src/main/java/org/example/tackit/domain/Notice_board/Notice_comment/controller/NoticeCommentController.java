package org.example.tackit.domain.Notice_board.Notice_comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Notice_board.Notice_comment.dto.req.NoticeCommentCreateDto;
import org.example.tackit.domain.Notice_board.Notice_comment.dto.req.NoticeCommentUpdateDto;
import org.example.tackit.domain.Notice_board.Notice_comment.dto.resp.NoticeCommentRespDto;
import org.example.tackit.domain.Notice_board.Notice_comment.service.NoticeCommentService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice-comments")
public class NoticeCommentController {
    private final NoticeCommentService noticeCommentService;

    // 1. 댓글 작성
    @PostMapping
    public ResponseEntity<NoticeCommentRespDto> createComment(
            @RequestBody NoticeCommentCreateDto req,
            @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        String org = user.getOrganization();

        NoticeCommentRespDto response = noticeCommentService.createComment(req, email, org);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<NoticeCommentRespDto>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        return ResponseEntity.ok(noticeCommentService.getCommentByPost(postId, org));
    }

    // 3. 댓글 수정
    @PatchMapping("{commentId}")
    public ResponseEntity<NoticeCommentRespDto> updateComment(
            @PathVariable long commentId,
            @RequestBody NoticeCommentUpdateDto req,
            @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        String org = user.getOrganization();
        NoticeCommentRespDto updateResp = noticeCommentService.updateComment(commentId, req, email, org);

        return ResponseEntity.ok(updateResp);
    }

    // 4. 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId, @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        String org = user.getOrganization();
        noticeCommentService.deleteComment(commentId, email, org);

        return ResponseEntity.noContent().build();
    }

    // 5. 댓글 신고
    @PostMapping("/{commentId}/report")
    public ResponseEntity<String> reportComment(@PathVariable long commentId, @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        noticeCommentService.increaseCommentReportCount(commentId, org);
        return ResponseEntity.ok("댓글을 신고하였습니다.");
    }
}
