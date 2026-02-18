package org.example.tackit.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.executive.dto.response.MemberListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/executive/members")
@RequiredArgsConstructor
public class ExecutiveMemberController {


    // [ 전체 회원 조회 ]
    /*
    @GetMapping
    public ResponseEntity<List<MemberListResponse>> getAllMembers(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(users);
    }

     */

    /*
    // [ 대기 회원 조회 ]
    @GetMapping("/deleted")
    public ResponseEntity<DeletedMemberResp> getDeletedMembers() {
        DeletedMemberResp deletedMembers = adminMemberService.getDeletedMembers();
        return ResponseEntity.ok(deletedMembers);
    }

    // [ 사용 중 회원 조회 ]

    // [ 탈퇴 회원 조회 ]

    // [ 회원 승인 ]
    // [ 회원 거부 ]
     */
}
