package org.example.tackit.domain.noticeBoard.Notice_comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.noticeBoard.Notice_comment.dto.req.NoticeCommentCreateDto;
import org.example.tackit.domain.noticeBoard.Notice_comment.dto.req.NoticeCommentUpdateDto;
import org.example.tackit.domain.noticeBoard.Notice_comment.dto.resp.NoticeCommentRespDto;
import org.example.tackit.domain.noticeBoard.Notice_comment.service.NoticeCommentService;
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
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        NoticeCommentRespDto response = noticeCommentService.createComment(req, user.getUsername(), profile.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<NoticeCommentRespDto>> getComments(
            @PathVariable Long postId,
            @ActiveProfile ProfileContext profile) {
        return ResponseEntity.ok(noticeCommentService.getCommentByPost(postId, profile.id()));
    }

    // 3. 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<NoticeCommentRespDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody NoticeCommentUpdateDto req,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        NoticeCommentRespDto updateResp = noticeCommentService.updateComment(commentId, req, user.getUsername(), profile.id());

        return ResponseEntity.ok(updateResp);
    }

    // 4. 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        noticeCommentService.deleteComment(commentId, user.getUsername(), profile.id());

        return ResponseEntity.noContent().build();
    }

    // 5. 댓글 신고
    @PostMapping("/{commentId}/report")
    public ResponseEntity<String> reportComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        noticeCommentService.increaseCommentReportCount(commentId, profile.id());
        return ResponseEntity.ok("댓글을 신고하였습니다.");
    }
}
