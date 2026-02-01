package org.example.tackit.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.notification.dto.resp.NotificationRespDto;
import org.example.tackit.domain.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // [ 실시간 알림을 위한 SSE 구독 ]
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        return notificationService.subscribe(userId);
    }

    // [ 모든 알림 조회 ]
    @GetMapping
    public ResponseEntity<List<NotificationRespDto>> getAllNotifications(
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
            ) {
        List<NotificationRespDto> allNotifications = notificationService.findAllNotifications(user.getId(), profile);

        return ResponseEntity.ok(allNotifications);
    }

    // [ 알림 읽음 ]
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails user) {

        String email = user.getEmail();
        notificationService.markAsRead(notificationId, email);
        return ResponseEntity.ok().build();
    }

}
