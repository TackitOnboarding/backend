package org.example.tackit.domain.QnA_board.QnA_post.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.QnA_board.QnA_post.dto.request.QnAPostReqDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.request.UpdateQnARequestDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.response.QnAPopularPostRespDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.response.QnAPostRespDto;
import org.example.tackit.domain.QnA_board.QnA_post.service.QnAPostService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna-posts")
public class QnAPostController {
    private final QnAPostService qnAPostService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<QnAPostRespDto> createQnAPost(
            @RequestPart("dto") QnAPostReqDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {
        String email = userDetails.getUsername(); // userDetails로부터 이메일 획득
        String org = userDetails.getOrganization();

        dto.setImageUrl(image);
        QnAPostRespDto response = qnAPostService.createPost(dto, email, org);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 게시글 수정
    @PutMapping("/{QnAPostId}")
    public ResponseEntity<QnAPostRespDto> updateQnAPost(
            @PathVariable("QnAPostId") long QnAPostId,
            @RequestPart("dto") UpdateQnARequestDto request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {
        String email = userDetails.getUsername();
        String org = userDetails.getOrganization();
        request.setImage(image);
        QnAPostRespDto updateResponse = qnAPostService.update(QnAPostId, request, email, org);

        return ResponseEntity.ok().body(updateResponse);
    }

    // 게시글 삭제
    @DeleteMapping("/{QnAPostId}")
    public ResponseEntity<QnAPostRespDto> deleteQnAPost(
            @PathVariable("QnAPostId") long QnAPostId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        String email = userDetails.getUsername();
        String org = userDetails.getOrganization();
        qnAPostService.delete(QnAPostId, email, org);

        return ResponseEntity.noContent().build();
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<QnAPostRespDto>> findAllQnAPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 5) Pageable pageable
    ) {
        String org = userDetails.getOrganization();
        PageResponseDTO<QnAPostRespDto> page = qnAPostService.findAll(org, pageable);
        return ResponseEntity.ok(page);
    }


    // 게시글 상세 조회
    @GetMapping("/{QnAPostId}")
    public ResponseEntity<QnAPostRespDto> findQnAPost(
            @PathVariable("QnAPostId") long QnAPostId ,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String org = userDetails.getOrganization();
        Long memberId = userDetails.getId();
        QnAPostRespDto post = qnAPostService.getPostById(QnAPostId, org, memberId);
        return ResponseEntity.ok(post);
    }

    // 게시글 신고
    @PostMapping("{QnAPostId}/report")
    public ResponseEntity<String> reportPost(
            @PathVariable long QnAPostId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String org = userDetails.getOrganization();
        String message = qnAPostService.reportQnAPost(QnAPostId, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    // 인기 3개
    @GetMapping("/popular")
    public ResponseEntity<List<QnAPopularPostRespDto>> getPopularPosts(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        String organization = user.getOrganization();
        List<QnAPopularPostRespDto> result = qnAPostService.getPopularPosts(organization);
        return ResponseEntity.ok(result);
    }
    
}
