package org.example.tackit.domain.organization.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.organization.dto.req.OrgCreateReqDto;
import org.example.tackit.domain.organization.dto.req.OrgJoinReqDto;
import org.example.tackit.domain.organization.dto.resp.OrgRespDto;
import org.example.tackit.domain.organization.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    // 모임 등록
    @PostMapping
    public ResponseEntity<OrgRespDto> createOrg(
            @RequestBody OrgCreateReqDto dto,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        OrgRespDto response = organizationService.createOrg(dto, email);
        return ResponseEntity.ok(response);
    }

    // 모임 참여
    @PostMapping("/{orgId}")
    public ResponseEntity<?> joinOrg(
            @PathVariable Long orgId,
            @RequestBody OrgJoinReqDto dto,
            Authentication authentication //
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        String email = authentication.getName();
        organizationService.joinOrg(orgId, email, dto);

        return ResponseEntity.ok("참여 신청이 완료되었습니다.");
    }

}
