package org.example.tackit.domain.auth.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.service.RejoinCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RejoinCheckController {
    private final RejoinCheckService rejoinCheckService;

    // 이메일 중복 확인 + 탈퇴 이력 조회 
    @GetMapping("/check-email-auth")
    public ResponseEntity<String> checkEmailAuth(@RequestParam String email) {
        String status = rejoinCheckService.checkEmailStatus(email);

        if ("DELETED".equals(status)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("탈퇴 이력이 있는 이메일입니다.");
        } else if ("ACTIVE".equals(status)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다.");
        }
    }

    // 탈퇴했던 회원 재가입
    @DeleteMapping("/rejoin")
    public ResponseEntity<String> rejoin(@RequestParam String email) {
        boolean result = rejoinCheckService.deleteDeletedMember(email);
        if (result) {
            return ResponseEntity.ok("재가입을 위한 삭제 완료. 새로 회원가입 진행해주세요.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 탈퇴 이력의 이메일이 없습니다.");
        }
    }

}
