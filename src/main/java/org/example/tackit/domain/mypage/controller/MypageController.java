package org.example.tackit.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.mypage.dto.response.MypageInfoResp;
import org.example.tackit.domain.mypage.service.MyPageService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MyPageService myPageService;

    // 내 정보 조회(닉네임, 조직(동아리라면 대학), 이메일)
    @GetMapping("/profiles/{memberOrgId}")
    public ResponseEntity<ApiResponse<MypageInfoResp>> getMyPageInfo(@PathVariable Long memberOrgId) {
        MypageInfoResp response = myPageService.getMypageInfo(memberOrgId);

        return ApiResponse.success(HttpStatus.OK, "마이 프로필 조회 성공", response);
    }
    /*

    // 스크랩한 자유 게시글
    @GetMapping("/free-scraps")
    public ResponseEntity<PageResponseDTO<FreeScrapResponse>> getMyFreeScraps(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 5, sort = "savedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(myPageFreeService.getScrapListByMember(user.getEmail(), pageable));
    }

    // 내가 작성한 자유 게시글
    @GetMapping("/free-posts")
    public ResponseEntity<PageResponseDTO<FreeMyPostResponseDto>> getMyFreePosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(myPageFreeService.getMyPosts(user.getUsername(), pageable));
    }

    // 내가 쓴 댓글 조회
    @GetMapping("/free-comments")
    public ResponseEntity<PageResponseDTO<FreeMyCommentResponseDto>> getMyFreeComments(
         @AuthenticationPrincipal CustomUserDetails user,
         @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(myPageFreeService.getMyComments(user.getUsername(), pageable));

    }

     */
}
