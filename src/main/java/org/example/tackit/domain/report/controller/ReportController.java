package org.example.tackit.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.report.dto.ReportRequestDto;
import org.example.tackit.domain.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

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
