package org.example.tackit.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.MemberDto;
import org.example.tackit.domain.admin.service.AdminMemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    // [ 전체 유저 조회 ]
    @GetMapping
    public ResponseEntity<Page<MemberDto>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MemberDto> users = adminMemberService.getAllMembers(pageable);
        return ResponseEntity.ok(users);
    }

    // [ 탈퇴 계정 ]
    @GetMapping("/deleted")
    public ResponseEntity<Page<MemberDto>> getDeletedMembers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MemberDto> deletedUsers = adminMemberService.getDeletedMembers(pageable);
        return ResponseEntity.ok(deletedUsers);
    }

}
