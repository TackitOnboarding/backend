package org.example.tackit.domain.poll.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.poll.dto.PollCreateReqDto;
import org.example.tackit.domain.poll.dto.PollDetailResDto;
import org.example.tackit.domain.poll.dto.PollSidebarResDto;
import org.example.tackit.domain.poll.dto.PollSimpleResDto;
import org.example.tackit.domain.poll.dto.PollUpdateReqDto;
import org.example.tackit.domain.poll.dto.VoteReqDto;
import org.example.tackit.domain.poll.service.PollService;
import org.example.tackit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/polls")
public class PollController {

  private final PollService pollService;

  // 투표 생성
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createPoll(
      @RequestBody @Valid PollCreateReqDto reqDto,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    Long pollId = pollService.createPoll(memberOrgId, reqDto);
    return ApiResponse.success(HttpStatus.CREATED, "투표 생성 성공", pollId);
  }

  // 투표 수정
  @PatchMapping("/{pollId:[0-9]+}")
  public ResponseEntity<ApiResponse<Void>> updatePoll(
      @PathVariable Long pollId,
      @RequestBody PollUpdateReqDto reqDto,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    pollService.updatePoll(pollId, memberOrgId, reqDto);
    return ApiResponse.success(HttpStatus.OK, "투표 수정 성공");
  }

  // 투표 삭제
  @DeleteMapping("/{pollId:[0-9]+}")
  public ResponseEntity<ApiResponse<Void>> deletePoll(
      @PathVariable Long pollId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    pollService.deletePoll(pollId, memberOrgId);
    return ApiResponse.success(HttpStatus.OK, "투표 삭제 성공");
  }

  // 투표 상세 조회
  @GetMapping("/{pollId:[0-9]+}")
  public ResponseEntity<ApiResponse<PollDetailResDto>> getPollDetail(
      @PathVariable Long pollId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    PollDetailResDto result = pollService.getPollDetail(pollId, memberOrgId);
    return ApiResponse.success(HttpStatus.OK, "투표 상세 조회 성공", result);
  }

  // 월간 투표 조회
  @GetMapping("/monthly")
  public ResponseEntity<ApiResponse<List<PollSimpleResDto>>> getMonthlyPolls(
      @RequestParam Long orgId,
      @RequestParam int year,
      @RequestParam int month,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    List<PollSimpleResDto> result = pollService.getMonthlyPolls(orgId, year, month, memberOrgId);
    return ApiResponse.success(HttpStatus.OK, "월간 투표 목록 조회 성공", result);
  }

  // 캘린더 사이드바 투표 조회 (마감 임박 + 진행 중)
  @GetMapping("/active")
  public ResponseEntity<ApiResponse<PollSidebarResDto>> getActivePolls(
      @RequestParam Long orgId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    PollSidebarResDto result = pollService.getSidebarPolls(orgId, memberOrgId);
    return ApiResponse.success(HttpStatus.OK, "진행 중인 투표 목록 조회 성공", result);
  }

  // 투표하기 / 재투표 / 투표 취소
  @PostMapping("/{pollId:[0-9]+}/vote")
  public ResponseEntity<ApiResponse<Void>> vote(
      @PathVariable Long pollId,
      @RequestBody @Valid VoteReqDto reqDto,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    pollService.vote(pollId, memberOrgId, reqDto);
    return ApiResponse.success(HttpStatus.OK, "투표 참여 완료");
  }
}