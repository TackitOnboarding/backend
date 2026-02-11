package org.example.tackit.domain.tipBoard.Tip_post.controller;

import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.tipBoard.Tip_post.dto.response.TipPopularPostRespDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.response.TipPostRespDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.response.TipScrapRespDto;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.tipBoard.Tip_post.dto.request.TipPostReqDto;
import org.example.tackit.domain.tipBoard.Tip_post.dto.request.TipPostUpdateDto;
import org.example.tackit.domain.tipBoard.Tip_post.service.TipPostService;
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
@RequestMapping("/api/tip-posts")
public class TipController {
    private final TipPostService tipService;

    public TipController(final TipPostService tipService) {
        this.tipService = tipService;
    }

    // 1. 게시글 전체 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<TipPostRespDto>> getAllPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponseDTO<TipPostRespDto> pageResponse = tipService.getActivePostsByOrganization(profile.id(), pageable);
        return ResponseEntity.ok(pageResponse);
    }

    // 2. 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<TipPostRespDto> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        TipPostRespDto post = tipService.getPostById(id, profile.id(), user.getId());
        return ResponseEntity.ok(post);
    }

    // 3. 게시글 작성
    @PostMapping
    public ResponseEntity<TipPostRespDto> create(
            @RequestPart("dto") TipPostReqDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) throws IOException {
        TipPostRespDto post = tipService.createPost(dto, user.getEmail(), profile.id(), image);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    // 4. 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<TipPostRespDto> update(
            @PathVariable Long id,
            @RequestPart("dto") TipPostUpdateDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) throws IOException {

        TipPostRespDto updatedPost = tipService.update(id, dto, user.getEmail(), profile.id(), image);
        return ResponseEntity.ok(updatedPost);
    }


    // 5. 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        tipService.deletePost(id, user.getEmail(), profile.id());
        return ResponseEntity.noContent().build();
    }

    // 6. 게시글 스크랩
    @PostMapping("/{id}/scrap")
    public ResponseEntity<TipScrapRespDto> scrapPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        TipScrapRespDto response = tipService.toggleScrap(id, user.getEmail(), profile.id());
        return ResponseEntity.ok(response);
    }

    // 7. 게시글 신고
    @PostMapping("/{id}/report")
    public ResponseEntity<String> reportPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile) {
        String message = tipService.report(id, user.getEmail(), profile.id());
        return ResponseEntity.ok(message);
    }

    // 인기 3개
    @GetMapping("/popular")
    public ResponseEntity<List<TipPopularPostRespDto>> getPopularPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
    ) {
        List<TipPopularPostRespDto> result = tipService.getPopularPosts(profile.id());
        return ResponseEntity.ok(result);
    }
}
