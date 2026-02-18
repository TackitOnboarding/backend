package org.example.tackit.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.executive.dto.response.MemberListResponse;
import org.example.tackit.domain.executive.service.ExecutiveMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executive/members")
@RequiredArgsConstructor
public class ExecutiveMemberController {
    private final ExecutiveMemberService executiveMemberService;

    // [ 전체 회원 조회 ]
    @GetMapping
    public ResponseEntity<List<MemberListResponse>> getAllMembers(
            @RequestParam Long orgId,
            @RequestParam(required = false) String orgStatus
    ) {
        List<MemberListResponse> responses = executiveMemberService.getMembers(orgId, orgStatus);
        return ResponseEntity.ok(responses);
    }

    // [ 멤버 승인 ]
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveMember(@RequestParam Long memberOrgId) {
        executiveMemberService.approveMember(memberOrgId);
        return ResponseEntity.noContent().build();
    }

    // [ 멤버 반려 ]
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectMember(@RequestParam Long memberOrgId) {
        executiveMemberService.rejectMember(memberOrgId);
        return ResponseEntity.noContent().build();
    }

}
