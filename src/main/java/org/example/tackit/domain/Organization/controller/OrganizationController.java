package org.example.tackit.domain.Organization.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Organization.dto.req.OrgCreateReqDto;
import org.example.tackit.domain.Organization.dto.req.OrgJoinReqDto;
import org.example.tackit.domain.Organization.service.OrganizationService;
import org.example.tackit.domain.entity.Org.OrgType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    // 모임 등록
    @PostMapping
    public ResponseEntity<?> createOrg(
            @RequestBody OrgCreateReqDto dto,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }

        String email = authentication.getName();
        organizationService.createOrg(dto, email);
        return ResponseEntity.ok("성공");
    }

    // 모임 참여
    @PostMapping("/{orgId}")
    public ResponseEntity<?> joinOrg(
            @PathVariable Long orgId,
            @RequestParam OrgType type,
            @RequestBody OrgJoinReqDto dto,
            Authentication authentication //
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        String email = authentication.getName();
        organizationService.joinOrg(orgId, type, email, dto);

        return ResponseEntity.ok("참여 신청이 완료되었습니다.");
    }

}
