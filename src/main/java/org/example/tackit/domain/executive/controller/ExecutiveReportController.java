package org.example.tackit.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.PageResponseDTO;
import org.example.tackit.domain.admin.dto.ReportedPostDTO;
import org.example.tackit.domain.entity.Post;
import org.example.tackit.domain.executive.dto.response.ReportedPostResDto;
import org.example.tackit.domain.executive.service.ExecutiveReportedPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/executive/reports")
@RequiredArgsConstructor
public class ExecutiveReportController {
    private final ExecutiveReportedPostService executiveReportedPostService;

    // [ 신고 게시글 전체 조회 ]
    @GetMapping
    public ResponseEntity<PageResponseDTO<ReportedPostResDto>> getReportList(
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @PageableDefault(size = 10, sort = "reportedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReportedPostResDto> reports = executiveReportedPostService.getReportList(status, pageable);
        return ResponseEntity.ok(PageResponseDTO.from(reports));
    }

    // [ 신고 게시글 상세 조회 ]
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportedPostResDto> getReportDetail(@PathVariable Long reportId) {
        return ResponseEntity.ok(executiveReportedPostService.getReportDetail(reportId));
    }

    // [ 신고 게시글 복구 ]

    // [ 신고 게시글 완전 삭제 ]
    /*
    @DeleteMapping("/{postType}/posts/{postId}")
    public ResponseEntity<Void> deleteReportedPost(
            @PathVariable("postType") Post postType,
            @PathVariable("postId") Long postId) {

        ReportedPostService service = reportedPostServices.get(postType);
        if (service == null) {
            throw new IllegalArgumentException("지원하지 않는 게시글 유형입니다: " + postType);
        }

        service.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 게시글 활성화
    @PatchMapping("/{postType}/posts/{postId}/activate")
    public ResponseEntity<String> activateReportedPost(
            @PathVariable("postType") Post postType,
            @PathVariable("postId") Long postId) {

        ReportedPostService service = reportedPostServices.get(postType);
        if (service == null) {
            throw new IllegalArgumentException("지원하지 않는 게시글 유형입니다: " + postType);
        }

        service.activatePost(postId);
        return ResponseEntity.ok("게시글이 활성화되었습니다.");
    }
         */
}
