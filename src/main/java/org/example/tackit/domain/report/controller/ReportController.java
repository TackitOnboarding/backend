package org.example.tackit.domain.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.report.dto.ReportReqDto;
import org.example.tackit.domain.report.service.ReportService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/posts/{postId}/report")
  public ResponseEntity<ApiResponse<Object>> reportPost(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long postId,
      @Valid @RequestBody ReportReqDto reqDto
  ) {
    reportService.reportPost(reqDto, profileContext.id(), postId);
    return ApiResponse.success(HttpStatus.OK, "게시글 신고 접수 성공");
  }

  @PostMapping("/comments/{commentId}/report")
  public ResponseEntity<ApiResponse<Object>> reportComment(
      @ActiveProfile ProfileContext profileContext,
      @PathVariable Long commentId,
      @Valid @RequestBody ReportReqDto reqDto
  ) {
    reportService.reportComment(reqDto, profileContext.id(), commentId);
    return ApiResponse.success(HttpStatus.OK, "댓글 신고 접수 성공");
  }


    /*
    @PostMapping("/create")
    public ResponseEntity<String> createReport(
            @RequestBody ReportRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();
        String org = userDetails.getOrganization();

        reportService.createReport(request, email, org);
        return ResponseEntity.ok("신고 사유가 접수되었습니다.");
    }

     */

}
