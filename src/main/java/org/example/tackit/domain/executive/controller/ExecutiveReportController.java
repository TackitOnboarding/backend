package org.example.tackit.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executive/reports")
@RequiredArgsConstructor
public class ExecutiveReportController {

    /*
    // [ 신고 게시글 전체 조회 ]
    @GetMapping("/{postType}/posts")
    public ResponseEntity<PageResponseDTO<ReportedPostDTO>> getReportedPosts(
            @PathVariable("postType") Post postType,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        ReportedPostService service = reportedPostServices.get(postType);

        if (service == null) {
            throw new IllegalArgumentException("지원하지 않는 게시글 유형입니다: " + postType);
        }

        Page<ReportedPostDTO> posts = service.getDeletedPosts(pageable);
        return ResponseEntity.ok(PageResponseDTO.from(posts));
    }

    // [ 신고 게시글 상세 조회 ]

    // [ 신고 게시글 복구 ]

    // [ 신고 게시글 완전 삭제 ]
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
