package org.example.tackit.domain.event.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.event.dto.EventCreateReqDto;
import org.example.tackit.domain.event.dto.EventDetailResDto;
import org.example.tackit.domain.event.dto.EventSimpleResDto;
import org.example.tackit.domain.event.dto.EventUpdateReqDto;
import org.example.tackit.domain.event.service.EventService;
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
@RequestMapping("/api/events")
public class EventController {

  private final EventService eventService;

  /**
   * 일정 생성
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createEvent(
      @RequestBody EventCreateReqDto reqDto,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    Long eventId = eventService.createEvent(reqDto, memberOrgId);

    return ApiResponse.success(HttpStatus.CREATED, "일정 생성 성공", eventId);
  }

  /**
   * 일정 수정
   */
  @PatchMapping("/{eventId}")
  public ResponseEntity<ApiResponse<Long>> updateEvent(
      @PathVariable Long eventId,
      @RequestBody EventUpdateReqDto reqDto,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();
    eventService.updateEvent(eventId, reqDto, memberOrgId);

    return ApiResponse.success(HttpStatus.OK, "일정 수정 성공", eventId);
  }

  /**
   * 월간 일정 조회
   */
  @GetMapping("/monthly")
  public ResponseEntity<ApiResponse<List<EventSimpleResDto>>> getMonthlyEvents(
      @RequestParam int year,
      @RequestParam int month,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();

    List<EventSimpleResDto> events = eventService.getMonthlyEvents(year, month, memberOrgId);

    return ApiResponse.success(HttpStatus.OK, "월간 일정 조회 성공", events);
  }

  /**
   * 다가오는 일정(사이드바) 조회
   */
  @GetMapping("/upcoming")
  public ResponseEntity<ApiResponse<List<EventSimpleResDto>>> getUpcomingEvents(
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();

    List<EventSimpleResDto> events = eventService.getUpcomingEvents(memberOrgId);

    return ApiResponse.success(HttpStatus.OK, "다가오는 일정 조회 성공", events);
  }

  /**
   * 일정 상세 조회
   */
  @GetMapping("/{eventId:[0-9]+}")
  public ResponseEntity<ApiResponse<EventDetailResDto>> getEventDetail(
      @PathVariable Long eventId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();

    EventDetailResDto detail = eventService.getEventDetail(eventId, memberOrgId);

    return ApiResponse.success(HttpStatus.OK, "일정 상세 조회 성공", detail);
  }

  /**
   * 일정 삭제
   */
  @DeleteMapping("/{eventId}")
  public ResponseEntity<ApiResponse<Void>> deleteEvent(
      @PathVariable Long eventId,
      @ActiveProfile ProfileContext profileContext
  ) {
    Long memberOrgId = profileContext.id();

    eventService.deleteEvent(eventId, memberOrgId);

    return ApiResponse.success(HttpStatus.OK, "일정 삭제 성공");
  }
}