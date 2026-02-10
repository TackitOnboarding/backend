package org.example.tackit.domain.event.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.event.dto.EventCreateReqDto;
import org.example.tackit.domain.event.dto.EventDetailResDto;
import org.example.tackit.domain.event.dto.EventSimpleResDto;
import org.example.tackit.domain.event.dto.EventUpdateReqDto;
import org.example.tackit.domain.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    /**
     * 일정 생성
     */
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody EventCreateReqDto reqDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // TODO 인증 정보가 없습니다 코드 공통 로직 처리
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        Long eventId = eventService.createEvent(reqDto, userDetails.getId());
        
        // TODO ResponseEntity 커스텀 공통 양식 추가
        return ResponseEntity.status(HttpStatus.CREATED).body(eventId);
    }

    /**
     * 일정 수정
     */
    @PatchMapping("/{eventId}")
    public ResponseEntity<?>  updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventUpdateReqDto reqDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        eventService.updateEvent(eventId, reqDto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(eventId);
    }

    /**
     * 월간 일정 조회
     */
    @GetMapping("/monthly")
    public ResponseEntity<?>  getMonthlyEvents(
            @RequestParam Long orgId,
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        List<EventSimpleResDto> events = eventService.getMonthlyEvents(orgId, year, month, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    /**
     * 다가오는 일정(사이드바) 조회
     */
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents(
            @RequestParam Long orgId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        List<EventSimpleResDto> events = eventService.getUpcomingEvents(orgId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

}