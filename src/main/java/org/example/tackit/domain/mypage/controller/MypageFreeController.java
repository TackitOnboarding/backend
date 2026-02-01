package org.example.tackit.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.mypage.dto.response.*;
import org.example.tackit.domain.mypage.service.MyPageFreeService;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageFreeController {
    private final MyPageFreeService myPageFreeService;

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
