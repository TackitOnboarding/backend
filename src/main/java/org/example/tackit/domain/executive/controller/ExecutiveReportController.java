package org.example.tackit.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.admin.dto.ReportedPostDetailDto;
import org.example.tackit.domain.admin.dto.ReportedPostDto;
import org.example.tackit.domain.executive.service.ExecutiveReportService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/executive/posts")
@RequiredArgsConstructor
public class ExecutiveReportController {
    private final ExecutiveReportService executiveReportService;

    // [ 신고 게시글 목록 조회 (전체/신고접수/비활성화) ]
    // 파라미터 type: "ALL", "PENDING", "DELETED" (기본값 ALL)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportedPostDto>>> getReportedPosts(
            @ActiveProfile ProfileContext profileContext,
            @RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ReportedPostDto> reportedPosts = executiveReportService.getReportedPosts(profileContext.id(), type, pageable);

        return ApiResponse.success(HttpStatus.OK, "신고 게시글 목록 조회 성공", reportedPosts);
    }

    // [ 신고 게시글 상세 조회 ]
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportedPostDetailDto>> getReportedPostDetail(
            @ActiveProfile ProfileContext profileContext,
            @PathVariable Long reportId) {
        ReportedPostDetailDto detail = executiveReportService.getReportedPostDetail(profileContext.id(), reportId);

        return ApiResponse.success(HttpStatus.OK, "신고 게시글 상세 조회 성공", detail);
    }


    // [ 신고 게시글 복구(활성화) ]
    @PatchMapping("/{postId}/activate")
    public ResponseEntity<ApiResponse<Void>> activatePost(
            @ActiveProfile ProfileContext profileContext,
            @PathVariable Long postId) {
        executiveReportService.activatePost(profileContext.id(), postId);

        return ApiResponse.success(HttpStatus.OK, "게시글 활성화 성공", null);

    }

    // [ 신고 게시글 완전 삭제 ]
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @ActiveProfile ProfileContext profileContext,
            @PathVariable Long postId) {
        executiveReportService.deletePost(profileContext.id(), postId);

        return ApiResponse.success(HttpStatus.OK, "게시글 완전 삭제 성공", null);
    }
}

