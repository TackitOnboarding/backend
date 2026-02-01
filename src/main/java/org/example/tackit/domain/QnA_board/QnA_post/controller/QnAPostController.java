package org.example.tackit.domain.QnA_board.QnA_post.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
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
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
            ) throws IOException {

        dto.setImageUrl(image);
        QnAPostRespDto response = qnAPostService.createPost(dto, user.getEmail(), profile.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 게시글 수정
    @PutMapping("/{QnAPostId}")
    public ResponseEntity<QnAPostRespDto> updateQnAPost(
            @PathVariable("QnAPostId") long QnAPostId,
            @RequestPart("dto") UpdateQnARequestDto request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
    ) throws IOException {
        request.setImage(image);
        QnAPostRespDto updateResponse = qnAPostService.update(QnAPostId, request, user.getEmail(), profile.id());

        return ResponseEntity.ok().body(updateResponse);
    }

    // 게시글 삭제
    @DeleteMapping("/{QnAPostId}")
    public ResponseEntity<QnAPostRespDto> deleteQnAPost(
            @PathVariable("QnAPostId") long QnAPostId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
    ){
        qnAPostService.delete(QnAPostId, user.getEmail(), profile.id());

        return ResponseEntity.noContent().build();
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<QnAPostRespDto>> findAllQnAPost(
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 5) Pageable pageable
    ) {
        PageResponseDTO<QnAPostRespDto> page = qnAPostService.findAll(profile.id(), pageable);
        return ResponseEntity.ok(page);
    }


    // 게시글 상세 조회
    @GetMapping("/{QnAPostId}")
    public ResponseEntity<QnAPostRespDto> findQnAPost(
            @PathVariable("QnAPostId") Long QnAPostId ,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
    ) {
        QnAPostRespDto post = qnAPostService.getPostById(QnAPostId, profile.id(), user.getId());
        return ResponseEntity.ok(post);
    }

    // 게시글 신고
    @PostMapping("{QnAPostId}/report")
    public ResponseEntity<String> reportPost(
            @PathVariable Long QnAPostId,
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
    ) {
        String message = qnAPostService.reportQnAPost(QnAPostId, profile.id());
        return ResponseEntity.ok(message);
    }

    // 인기 3개
    @GetMapping("/popular")
    public ResponseEntity<List<QnAPopularPostRespDto>> getPopularPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
    ) {
        List<QnAPopularPostRespDto> result = qnAPostService.getPopularPosts(profile.id());
        return ResponseEntity.ok(result);
    }
    
}
