package org.example.tackit.domain.Free_board.Free_post.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_post.dto.request.FreePostReqDto;
import org.example.tackit.domain.Free_board.Free_post.dto.request.UpdateFreeReqDto;
import org.example.tackit.domain.Free_board.Free_post.dto.response.FreePopularPostRespDto;
import org.example.tackit.domain.Free_board.Free_post.dto.response.FreePostRespDto;
import org.example.tackit.domain.Free_board.Free_post.dto.response.FreeScrapResponseDto;
import org.example.tackit.domain.Free_board.Free_post.service.FreePostService;
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
@RequestMapping("/api/free-posts")
@RequiredArgsConstructor
public class FreePostController {
    private final FreePostService freePostService;

    // 1. 게시글 전체 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<FreePostRespDto>> getAllPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        String org = user.getOrganization();
        PageResponseDTO<FreePostRespDto> pageResponse = freePostService.findAll(org, pageable);
        return ResponseEntity.ok(pageResponse);
    }

    // 2. 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<FreePostRespDto> findFreePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        FreePostRespDto post = freePostService.getPostById(id, org, user.getId());
        return ResponseEntity.ok(post);
    }

    // 3. 게시글 작성
    @PostMapping
    public ResponseEntity<FreePostRespDto> createFreePost(
            @RequestPart("dto") FreePostReqDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {
        String email = user.getEmail();
        String org = user.getOrganization();
        dto.setImage(image);
        FreePostRespDto resp = freePostService.createPost(dto, email, org);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 4. 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<FreePostRespDto> updateFreePost(
            @PathVariable Long id,
            @RequestPart("dto") UpdateFreeReqDto req,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {

        req.setImage(image);
        String email = user.getEmail();
        String org = user.getOrganization();
        FreePostRespDto updateResp = freePostService.update(id, req, email, org);

        return ResponseEntity.ok(updateResp);
    }

    // 5. 게시글 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<FreePostRespDto> deleteFreePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getEmail();
        String org = user.getOrganization();
        freePostService.delete(id, email, org);

        return ResponseEntity.noContent().build();
    }

    // 6. 게시글 신고
    @PostMapping("{id}/report")
    public ResponseEntity<String> reportPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {

        String message = freePostService.report(id, user.getId());
        return ResponseEntity.ok(message);
    }

    // 7. 게시글 스크랩
    @PostMapping("/{id}/scrap")
    public ResponseEntity<FreeScrapResponseDto> scrapPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        String org = user.getOrganization();
        String email = user.getEmail();
        FreeScrapResponseDto response = freePostService.toggleScrap(id, email, org);

        return ResponseEntity.ok(response);
    }

    // 인기 3개
    @GetMapping("/popular")
    public ResponseEntity<List<FreePopularPostRespDto>> getPopularPosts(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        String organization = user.getOrganization();
        List<FreePopularPostRespDto> result = freePostService.getPopularPosts(organization);
        return ResponseEntity.ok(result);
    }
}
