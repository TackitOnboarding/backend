//package org.example.tackit.domain.admin.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.tackit.domain.admin.service.AdminDashboardService;
//import org.example.tackit.domain.admin.service.AdminMemberService;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//@RequestMapping("/api/admin/dashboard")
//@RequiredArgsConstructor
//public class AdminDashboardController {
//
//  private final AdminMemberService adminMemberService;
//  private final AdminDashboardService adminDashboardService;
//
//    /*
//    // [ DAU ]
//    @GetMapping("/users/dau")
//    public ResponseEntity<Long> getDauCount() {
//        Long dau = adminDashboardService.getDau(LocalDate.now());
//        return ResponseEntity.ok(dau);
//    }
//
//    // [ MAU ]
//    @GetMapping("/users/mau")
//    public ResponseEntity<Long> getMauCount() {
//        Long mau = adminDashboardService.getMau(LocalDate.now());
//        return ResponseEntity.ok(mau);
//    }
//
//    // [ 총 게시글 수 ]
//    @GetMapping("/posts/count")
//    public ResponseEntity<Long> getPostsCount() {
//        long count = adminDashboardService.getPostsCount();
//        return ResponseEntity.ok(count);
//    }
//
//    // [ 신고로 비활성화된 게시글 수 ]
//    @GetMapping("/deleted-posts/count")
//    public ResponseEntity<Long> getDeletedPostsCount() {
//        long count = adminDashboardService.getDeletedPostsByReport();
//        return ResponseEntity.ok(count);
//    }
//
//    // [ 신고 사유 전체 조회  ]
//    @GetMapping("/reports")
//    public ResponseEntity<PageResponseDTO<ReportListDto>> getAllReports(
//            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        Page<ReportListDto> reports = adminDashboardService.findAllReports(pageable);
//
//        PageResponseDTO<ReportListDto> responseDTO = PageResponseDTO.from(reports);
//
//        return ResponseEntity.ok(responseDTO);
//    }
//
//    // [ 개별 신고 사유 조회 ]
//    @GetMapping("/reports/{targetId}")
//    public ResponseEntity<ReportContentDetailDto> getReportDetails(
//            @PathVariable Long targetId,
//            @RequestParam TargetType targetType
//            ) {
//
//        ReportContentDetailDto details = adminDashboardService.findReportDetails(targetId, targetType);
//        return ResponseEntity.ok(details);
//    }
//
//    // [ 회원 가입 통계 ]
//    @GetMapping("/user-statistics")
//    public ResponseEntity<MemberStatisticsDTO> getMemberStatistics() {
//        MemberStatisticsDTO stats = adminMemberService.getMemberStatistics();
//        return ResponseEntity.ok(stats);
//    }
//
//     */
//
//}
