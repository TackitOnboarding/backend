package org.example.tackit.domain.organization.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.entity.org.OrgType;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;
import org.example.tackit.domain.memberOrg.service.MemberOrgService;
import org.example.tackit.domain.organization.dto.req.OrgCreateReqDto;
import org.example.tackit.domain.organization.dto.req.OrgJoinReqDto;
import org.example.tackit.domain.organization.dto.resp.OrgRespDto;
import org.example.tackit.domain.organization.service.OrganizationService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrganizationController {

  private final OrganizationService organizationService;
  private final MemberOrgService memberOrgService;

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

  // 모임 검색
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<List<OrgRespDto>>> searchOrgs(
      @RequestParam OrgType orgType,
      @RequestParam String orgName
  ) {
    List<OrgRespDto> results = organizationService.searchOrgsByTypeAndName(orgType, orgName);
    return ApiResponse.success(HttpStatus.OK, "모임 검색 성공", results);
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

  @GetMapping("/{orgId}/members")
  public ResponseEntity<ApiResponse<Page<SimpleMemberProfileDto>>> getOrgMembers(
      @PathVariable Long orgId,
      @PageableDefault(size = 10, sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    Page<SimpleMemberProfileDto> members = memberOrgService.getOrgMembers(orgId, memberOrgId, pageable);
    return ApiResponse.success(HttpStatus.OK, "모임 소속 인원 조회 성공", members);
  }
}
