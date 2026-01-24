package org.example.tackit.domain.Free_board.Free_comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_comment.dto.req.FreeCommentCreateDto;
import org.example.tackit.domain.Free_board.Free_comment.dto.req.FreeCommentUpdateDto;
import org.example.tackit.domain.Free_board.Free_comment.dto.resp.FreeCommentRespDto;
import org.example.tackit.domain.Free_board.Free_comment.service.FreeCommentService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-comments")
public class FreeCommentController {
    private final FreeCommentService freeCommentService;

    // 1. 댓글 작성
    @PostMapping
    public ResponseEntity<FreeCommentRespDto> createComment(
            @RequestBody FreeCommentCreateDto req,
            @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        String org = user.getOrganization();

        FreeCommentRespDto response = freeCommentService.createComment(req, email, org);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<FreeCommentRespDto>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        return ResponseEntity.ok(freeCommentService.getCommentByPost(postId, org));
    }

    // 3. 댓글 수정
    @PatchMapping("{commentId}")
    public ResponseEntity<FreeCommentRespDto> updateComment(
            @PathVariable long commentId,
            @RequestBody FreeCommentUpdateDto req,
            @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        String org = user.getOrganization();
        FreeCommentRespDto updateResp = freeCommentService.updateComment(commentId, req, email, org);

        return ResponseEntity.ok(updateResp);
    }

    // 4. 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId, @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        String org = user.getOrganization();
        freeCommentService.deleteComment(commentId, email, org);

        return ResponseEntity.noContent().build();
    }

    // 5. 댓글 신고
    @PostMapping("/{commentId}/report")
    public ResponseEntity<String> reportComment(@PathVariable long commentId, @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        freeCommentService.increaseCommentReportCount(commentId, org);
        return ResponseEntity.ok("댓글을 신고하였습니다.");
    }

}

