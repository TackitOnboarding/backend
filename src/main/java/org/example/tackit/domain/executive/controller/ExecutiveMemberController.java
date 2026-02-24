package org.example.tackit.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.executive.dto.response.MemberListResDto;
import org.example.tackit.domain.executive.service.ExecutiveMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executive/members")
@RequiredArgsConstructor
public class ExecutiveMemberController {
    private final ExecutiveMemberService executiveMemberService;

    // [ 전체 회원 조회 ]
    @GetMapping
    public ResponseEntity<List<MemberListResDto>> getAllMembers(
            @RequestParam Long orgId,
            @RequestParam(value = "orgStatus", defaultValue = "ALL") String orgStatus,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<MemberListResDto> responses = executiveMemberService.getMembers(orgId, userDetails.getUsername(), orgStatus);
        return ResponseEntity.ok(responses);
    }

    // [ 멤버 승인 ]
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveMember(
            @RequestParam Long orgId,
            @RequestParam Long memberOrgId,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        executiveMemberService.approveMember(orgId, userDetails.getUsername(), memberOrgId);
        return ResponseEntity.noContent().build();
    }

    // [ 멤버 반려 ]
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectMember(
            @RequestParam Long orgId,
            @RequestParam Long memberOrgId,
            @AuthenticationPrincipal UserDetails userDetails) {
        executiveMemberService.rejectMember(orgId, userDetails.getUsername(), memberOrgId );
        return ResponseEntity.noContent().build();
    }

}
