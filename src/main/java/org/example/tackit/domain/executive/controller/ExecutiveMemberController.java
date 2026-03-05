package org.example.tackit.domain.executive.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.entity.org.OrgStatus;
import org.example.tackit.domain.executive.dto.response.MemberListResDto;
import org.example.tackit.domain.executive.service.ExecutiveMemberService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executive/members")
@RequiredArgsConstructor
public class ExecutiveMemberController {

  private final ExecutiveMemberService executiveMemberService;

  // [ 전체 회원 조회 ]
  @GetMapping
  public ResponseEntity<ApiResponse<List<MemberListResDto>>> getAllMembers(
      @RequestParam(value = "orgStatus", defaultValue = "ALL") String orgStatus,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long requesterMemberOrgId = profileContext.id();
    List<MemberListResDto> responses = executiveMemberService.getMembers(requesterMemberOrgId,
        orgStatus);
    return ApiResponse.success(HttpStatus.OK, "전체 회원 조회 성공", responses);
  }

  // [ 멤버 승인 ]
  @PatchMapping("/approve")
  public ResponseEntity<ApiResponse<Object>> approveMember(
      @RequestParam Long memberOrgId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long requesterMemberOrgId = profileContext.id();
    executiveMemberService.updateMemberApplicationStatus(requesterMemberOrgId, memberOrgId,
        OrgStatus.ACTIVE);
    return ApiResponse.success(HttpStatus.OK, "가입 승인 성공");
  }

  // [ 멤버 반려 ]
  @PatchMapping("/reject")
  public ResponseEntity<ApiResponse<Object>> rejectMember(
      @RequestParam Long memberOrgId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long requesterMemberOrgId = profileContext.id();
    executiveMemberService.updateMemberApplicationStatus(requesterMemberOrgId, memberOrgId,
        OrgStatus.REJECTED);
    return ApiResponse.success(HttpStatus.OK, "가입 반려 성공");
  }

}
