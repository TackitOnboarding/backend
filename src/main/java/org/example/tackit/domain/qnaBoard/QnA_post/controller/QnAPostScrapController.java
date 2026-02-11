package org.example.tackit.domain.qnaBoard.QnA_post.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.qnaBoard.QnA_post.dto.response.QnACheckScrapResponseDto;
import org.example.tackit.domain.qnaBoard.QnA_post.dto.response.QnAScrapResponseDto;
import org.example.tackit.domain.qnaBoard.QnA_post.service.QnAScrapService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna-posts")
public class QnAPostScrapController {
    private final QnAScrapService qnAScrapService;

    // 찜하기

    @PostMapping("/{postId}/scrap")
    public ResponseEntity<QnAScrapResponseDto> toggleScrap(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        String org = userDetails.getOrganization();
        String email = userDetails.getUsername();
        QnAScrapResponseDto response = qnAScrapService.toggleScrap(postId, email, org);
        return ResponseEntity.ok(response);
    }

    // 찜한 글 보기
    @GetMapping("/scrap")
    public ResponseEntity<PageResponseDTO<QnACheckScrapResponseDto>> getMyScraps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 5, sort = "savedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(qnAScrapService.getMyQnAScraps(userDetails.getEmail(), pageable));
    }

}

 */
