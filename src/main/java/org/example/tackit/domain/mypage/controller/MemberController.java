package org.example.tackit.domain.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.mypage.dto.request.UpdatePasswordRequest;
import org.example.tackit.domain.mypage.dto.request.UpdateNicknameRequest;
import org.example.tackit.domain.mypage.dto.response.UpdateNicknameResponse;
import org.example.tackit.domain.mypage.dto.response.UpdatePasswordResponse;
import org.example.tackit.domain.mypage.dto.response.UpdateProfileImageResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final UpdateMemberService updateMemberService;

    // 내정보 조회
    @GetMapping("/me")
    public ResponseEntity<MemberMypageResponse> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername(); // Spring Security 내부적으로 email이 username

        MemberMypageResponse response = memberService.getMyPageInfo(email);
        return ResponseEntity.ok(response);
    }

    // 닉네임 변경
    @PatchMapping("/nickname")
    public ResponseEntity<UpdateNicknameResponse> updateNickname(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateNicknameRequest request) {
        UpdateNicknameResponse response = updateMemberService.changeNickname(
                userDetails.getUsername(),
                request.getNickname()
        );
        return ResponseEntity.ok(response);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @Valid @RequestBody UpdatePasswordRequest request) {
        UpdatePasswordResponse response = updateMemberService.updatePassword(
                userDetails.getUsername(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(response);
    }

    // 프로필 이미지 업로드
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateProfileImageResponse> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("image") MultipartFile imageFile) throws IOException {
        UpdateProfileImageResponse response =
                updateMemberService.uploadProfileImage(userDetails.getUsername(), imageFile);
        return ResponseEntity.ok(response);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/profile-image")
    public ResponseEntity<String> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        updateMemberService.deleteProfileImage(userDetails.getUsername());
        return ResponseEntity.ok("프로필 이미지가 삭제되었습니다.");
    }


}

 */