package org.example.tackit.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.dto.SignInResponse;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.auth.login.service.AuthService;
import org.example.tackit.domain.mypage.dto.request.UpdateProfileReq;
import org.example.tackit.domain.mypage.dto.response.MyCommentListResp;
import org.example.tackit.domain.mypage.dto.response.MyPostListResp;
import org.example.tackit.domain.mypage.dto.response.MyPageInfoResp;
import org.example.tackit.domain.mypage.dto.response.MyScrapListResp;
import org.example.tackit.domain.mypage.service.MyPageService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MyPageService myPageService;
    private final AuthService authService;

    // 내 정보 조회(닉네임, 조직(동아리라면 대학), 이메일)
    @GetMapping("/profiles/{memberOrgId}")
    public ResponseEntity<ApiResponse<MyPageInfoResp>> getMyPageInfo(
            @PathVariable Long memberOrgId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MyPageInfoResp response = myPageService.getMypageInfo(memberOrgId, userDetails.getUsername());

        return ApiResponse.success(HttpStatus.OK, "마이 프로필 조회 성공", response);
    }

    // 비밀번호, 닉네임, 프로필 이미지 수정
    @PatchMapping("/profiles/{memberOrgId}")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @PathVariable Long memberOrgId,
            @RequestBody UpdateProfileReq request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        myPageService.updateProfile(memberOrgId, request, userDetails.getUsername());

        return ApiResponse.success(HttpStatus.OK, "프로필 수정 성공", null);
    }

    // 작성한 글 조회
    @GetMapping("/posts/{memberOrgId}")
    public ResponseEntity<ApiResponse<List<MyPostListResp>>> getMyPosts(
            @PathVariable Long memberOrgId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<MyPostListResp> response = myPageService.getMyPosts(memberOrgId, userDetails.getUsername());

        return ApiResponse.success(HttpStatus.OK, "작성한 글 목록 조회 성공", response);
    }

    // 작성한 댓글 조회
    @GetMapping("/comments/{memberOrgId}")
    public ResponseEntity<ApiResponse<List<MyCommentListResp>>> getMyComments(
            @PathVariable Long memberOrgId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<MyCommentListResp> response = myPageService.getMyComments(memberOrgId, userDetails.getUsername());
        return ApiResponse.success(HttpStatus.OK, "작성한 댓글 목록 조회 성공", response);
    }

    // 스크랩 게시글 조회
    @GetMapping("/scraps/{memberOrgId}")
    public ResponseEntity<ApiResponse<List<MyScrapListResp>>> getMyScraps(
            @PathVariable Long memberOrgId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<MyScrapListResp> response = myPageService.getMyScraps(memberOrgId, userDetails.getUsername());
        return ApiResponse.success(HttpStatus.OK, "스크랩한 글 목록 조회 성공", response);
    }

    // 프로필 전환
    @PostMapping("/switch")
    public ResponseEntity<SignInResponse> switchProfile() {
        return ResponseEntity.ok(authService.switchProfile());
    }

}
