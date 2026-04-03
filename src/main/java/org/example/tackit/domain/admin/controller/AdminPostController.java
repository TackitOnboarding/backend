package org.example.tackit.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.ReportedPostDetailDto;
import org.example.tackit.domain.admin.dto.ReportedPostDto;
import org.example.tackit.domain.admin.service.AdminPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final AdminPostService adminPostService;

    // [ 신고 게시글 목록 조회 (전체/신고접수/비활성화) ]
    // 파라미터 type: "ALL", "PENDING", "DELETED" (기본값 ALL)
    @GetMapping
    public ResponseEntity<Page<ReportedPostDto>> getReportedPosts(
            @RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
            @PageableDefault(size = 10, sort = "reportedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ReportedPostDto> reportedPosts = adminPostService.getReportedPosts(type, pageable);
        return ResponseEntity.ok(reportedPosts);
    }

    // [ 신고 게시글 상세 조회 ]
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportedPostDetailDto> getReportedPostDetail(@PathVariable Long reportId) {
        ReportedPostDetailDto detail = adminPostService.getReportedPostDetail(reportId);
        return ResponseEntity.ok(detail);
    }


    // [ 신고 게시글 복구(활성화) ]
    @PatchMapping("/{postId}/activate")
    public ResponseEntity<Void> activatePost(@PathVariable Long postId) {
        adminPostService.activatePost(postId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // [ 신고 게시글 완전 삭제 ]
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminPostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
