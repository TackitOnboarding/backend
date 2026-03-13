package org.example.tackit.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.mypage.dto.response.MyCommentListResp;
import org.example.tackit.domain.mypage.dto.response.MyPostListResp;
import org.example.tackit.domain.mypage.dto.response.MyPageInfoResp;
import org.example.tackit.domain.mypage.dto.response.MyScrapListResp;
import org.example.tackit.domain.mypage.service.MyPageService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MyPageService myPageService;

    // 내 정보 조회(닉네임, 조직(동아리라면 대학), 이메일)
    @GetMapping("/profiles/{memberOrgId}")
    public ResponseEntity<ApiResponse<MyPageInfoResp>> getMyPageInfo(@PathVariable Long memberOrgId) {
        MyPageInfoResp response = myPageService.getMypageInfo(memberOrgId);

        return ApiResponse.success(HttpStatus.OK, "마이 프로필 조회 성공", response);
    }

    // 작성한 글 조회
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<MyPostListResp>>> getMyPosts(@RequestParam Long memberOrgId) {
        List<MyPostListResp> response = myPageService.getMyPosts(memberOrgId);

        return ApiResponse.success(HttpStatus.OK, "작성한 글 목록 조회 성공", response);
    }

    // 작성한 댓글 조회
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<List<MyCommentListResp>>> getMyComments(@RequestParam Long memberOrgId) {
        List<MyCommentListResp> response = myPageService.getMyComments(memberOrgId);
        return ApiResponse.success(HttpStatus.OK, "작성한 댓글 목록 조회 성공", response);
    }

    // 스크랩 게시글 조회
    @GetMapping("/scraps")
    public ResponseEntity<ApiResponse<List<MyScrapListResp>>> getMyScraps(@RequestParam Long memberOrgId) {

        List<MyScrapListResp> response = myPageService.getMyScraps(memberOrgId);
        return ApiResponse.success(HttpStatus.OK, "스크랩한 글 목록 조회 성공", response);
    }

    /*
    // 비밀번호 및 닉네임 수정
    @PatchMapping("/profiles/{memberOrgId}")

    // 프로필 전환
    @PostMapping("/{memberOrgId}/switch")

     */
}
