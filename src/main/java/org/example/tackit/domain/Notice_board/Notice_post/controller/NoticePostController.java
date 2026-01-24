package org.example.tackit.domain.Notice_board.Notice_post.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.PageResponseDTO;
import org.example.tackit.domain.Notice_board.Notice_post.dto.request.NoticePostReqDto;
import org.example.tackit.domain.Notice_board.Notice_post.dto.request.UpdateNoticeReqDto;
import org.example.tackit.domain.Notice_board.Notice_post.dto.response.NoticePostRespDto;
import org.example.tackit.domain.Notice_board.Notice_post.dto.response.NoticeScrapRespDto;
import org.example.tackit.domain.Notice_board.Notice_post.service.NoticePostService;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/notice-posts")
@RequiredArgsConstructor
public class NoticePostController {

    private final NoticePostService noticePostService;
    // 1. 게시글 전체 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<NoticePostRespDto>> getAllPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        String org = user.getOrganization();
        PageResponseDTO<NoticePostRespDto> pageResponse = noticePostService.findAll(org, pageable);
        return ResponseEntity.ok(pageResponse);
    }

    // 2. 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<NoticePostRespDto> findNoticePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        NoticePostRespDto post = noticePostService.getPostById(id, org, user.getId());
        return ResponseEntity.ok(post);
    }

    // 3. 게시글 작성
    @PostMapping
    public ResponseEntity<NoticePostRespDto> createNoticePost(
            @RequestPart("dto") NoticePostReqDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {

        // dto.setImage(image);
        // NoticePostRespDto resp = noticePostService.createPost(dto, email, org);
        NoticePostRespDto resp = noticePostService.createPost(dto, image, user.getEmail(), user.getOrganization());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 4. 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<NoticePostRespDto> updateNoticePost(
            @PathVariable Long id,
            @RequestPart("dto") UpdateNoticeReqDto req,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {

        // req.setImage(image);
        NoticePostRespDto resp = noticePostService.update(id, req, image, user.getEmail(), user.getOrganization());

        return ResponseEntity.ok(resp);
    }

    // 5. 게시글 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteNoticePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getEmail();
        String org = user.getOrganization();
        noticePostService.delete(id, email, org);

        return ResponseEntity.noContent().build();
    }

    // 6. 게시글 스크랩
    @PostMapping("/{id}/scrap")
    public ResponseEntity<NoticeScrapRespDto> scrapPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        String email = user.getEmail();
        NoticeScrapRespDto response = noticePostService.toggleScrap(id, email, org);

        return ResponseEntity.ok(response);
    }

}


