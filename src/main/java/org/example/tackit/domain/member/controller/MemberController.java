package org.example.tackit.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.member.dto.resp.MemberProfileResp;
import org.example.tackit.domain.member.dto.resp.MyInfoResp;
import org.example.tackit.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MyInfoResp>  getMyProfiles(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MyInfoResp response = memberService.getMyInfo(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
