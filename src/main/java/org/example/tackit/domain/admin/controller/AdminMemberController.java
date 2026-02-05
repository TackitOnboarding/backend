package org.example.tackit.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.DeletedMemberResp;
import org.example.tackit.domain.admin.dto.MemberDTO;
import org.example.tackit.domain.admin.service.AdminMemberService;
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

    /*
    // [ 전체 유저 조회 ]
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllUsers() {
        List<MemberDTO> users = adminMemberService.getAllMembersOrderByStatus();
        return ResponseEntity.ok(users);
    }

    // [ 탈퇴 계정 ]
    @GetMapping("/deleted")
    public ResponseEntity<DeletedMemberResp> getDeletedMembers() {
        DeletedMemberResp deletedMembers = adminMemberService.getDeletedMembers();
        return ResponseEntity.ok(deletedMembers);
    }

     */
}
