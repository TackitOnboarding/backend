package org.example.tackit.domain.auth.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.dto.*;
import org.example.tackit.domain.auth.login.service.AuthService;
import org.example.tackit.domain.auth.login.service.CheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CheckService checkService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpDto signUpDto) {
        authService.signup(signUpDto);
        return ResponseEntity.ok().body(Map.of("status", "OK", "message", "회원가입 성공했습니다."));
    }

    // 로그인
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInDto signInDto) {
        // TokenDto tokenDto = authService.signIn(signInDto);
        return ResponseEntity.ok(authService.signIn(signInDto));
        // return ResponseEntity.ok(tokenDto);
    }

    // 토큰 재발급
    // 만료된 Access Token 대신 유효한 Refresh Token으로 새로운 Access Token을 발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissueToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        TokenDto tokenDto = authService.reissue(bearerToken);
        return ResponseEntity.ok(tokenDto);
    }

    // 이메일 찾기
    /*
    @PostMapping("/find-email")
    public ResponseEntity<FindEmailRespDto> findEmail(
            @RequestBody FindEmailReqDto findEmailReqDto
    ) {
        FindEmailRespDto resp = authService.findEmailbyOrgAndNickname(
                findEmailReqDto.getOrganization(),
                findEmailReqDto.getName()
        );

        return ResponseEntity.ok(resp);
    }

    // 비밀번호 찾기
    @PostMapping("/find-password")
    public ResponseEntity<ResetTokenDto> findPassword(
            @RequestBody FindPwReqDto findPwReqDto
    ) {
        ResetTokenDto tokenDto = authService.findPwByIdentity(
                findPwReqDto.getName(),
                findPwReqDto.getOrganization(),
                findPwReqDto.getEmail()
        );

        return ResponseEntity.ok(tokenDto);
    }

     */

    // 비밀번호 재설정
    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ResetPwReqDto resetPwReqDto
    ) {
        authService.resetPassword(authorizationHeader, resetPwReqDto.getNewPassword());

        return ResponseEntity.ok().body(Map.of("status", "OK", "message", "비밀번호가 성공적으로 변경되었습니다."));
    }
}

