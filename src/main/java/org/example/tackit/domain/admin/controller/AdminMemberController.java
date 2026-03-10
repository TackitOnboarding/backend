package org.example.tackit.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.DeletedMemberResp;
import org.example.tackit.domain.admin.dto.MemberDTO;
import org.example.tackit.domain.admin.service.AdminMemberService;
import org.example.tackit.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    // [ 전체 유저 조회 ]
    @GetMapping
    public ResponseEntity<Page<MemberDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MemberDTO> users = adminMemberService.getAllMembers(pageable);
        return ResponseEntity.ok(users);
    }

    // [ 탈퇴 계정 ]
    @GetMapping("/deleted")
    public ResponseEntity<Page<MemberDTO>> getDeletedMembers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MemberDTO> deletedUsers = adminMemberService.getDeletedMembers(pageable);
        return ResponseEntity.ok(deletedUsers);
    }

}
