package org.example.tackit.domain.notification.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.admin.repository.AdminMemberRepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Notification;
import org.example.tackit.domain.notification.dto.NotificationEventDto;
import org.example.tackit.domain.notification.dto.resp.NotificationRespDto;
import org.example.tackit.domain.notification.repository.EmitterRepository;
import org.example.tackit.domain.notification.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    // 타임아웃 시간 - 150초
    private static final Long DEFAULT_TIMEOUT = 150L * 1000;

    private static final String EVENT_NAME_SSE = "sse";
    private static final String EVENT_NAME_NOTIFICATION = "notification";
    private static final String EVENT_NAME_HEARTBEAT = "heartbeat";

    private final EmitterRepository emitterRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationRepository notificationRepository;
    private final AdminMemberRepository adminMemberRepository;
    private final MemberOrgRepository memberOrgRepository;

    // [ 클라이언트 -> 알림 구독 신청 ]
    // 클라이언트가 서버에 처음 연결 요청
    public SseEmitter subscribe(Long userId) {
        // 1. Emitter 생성
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        // 2. 연결 끊어지거나 타임아웃되면 Emitter 제거하도록
        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));

        // 3. 연결 생성 직후, 준비 완료되었다는 메세지 전송
        String connectMessage = "EventStream Created. [userId = " + userId + "]";
        sendEventToEmitter(emitter, userId, EVENT_NAME_SSE, connectMessage);

        return emitter;
    }

    // [ 알림 이벤트 발생  ]
    // 댓글이 달리는 등 알림 이벤트 발생 시, 시스템에 이벤트 발생 사실 전달
    @Transactional
    public void send(Notification notification) {
        // 1. 알림 DB에 저장
        notificationRepository.save(notification);

        // 2. 이벤트 발행
        applicationEventPublisher.publishEvent(new NotificationEventDto(
                notification.getMember(),
                notification.getType(),
                notification.getMessage(),
                notification.getRelatedUrl()
        ));
    }

    // [ 발행된 알림 이벤트를 수신받아 실제 클라이언트에게 알림 전달  ]
    // 트랜잭션이 성공적으로 커밋된 후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deliverNotification(NotificationEventDto event) {
        // 1. 이벤트에서 수신자 정보 가져오기
        Long userId = event.getReceiver().getId();
        String message = event.getMessage();

        emitterRepository.findById(userId).ifPresent(emitter -> {
            sendEventToEmitter(emitter, userId, EVENT_NAME_NOTIFICATION, message);
        });
    }

    // [ 하트비트 전송 ]
    // 주기적으로 하트비트 전송하여 연결 유지 및 좀비 커넥션 정리
    @Scheduled(fixedRate = 25000) // 25초마다 실행
    public void sendHeartbeat() {
        Map<Long, SseEmitter> emitters = emitterRepository.findAll();
        emitters.forEach((userId, emitter) -> {
            sendEventToEmitter(emitter, userId, EVENT_NAME_HEARTBEAT, "HeartBeat OK");
        });
    }

    // [ 모든 알림 조회 ]
    @Transactional(readOnly = true)
    public List<NotificationRespDto> findAllNotifications(Long userId, ProfileContext profile) {
        // 1. 읽지 않은 모든 알림 조회
        List<Notification> notifications = notificationRepository.findAllByMemberIdAndMemberOrgIdOrderByCreatedAtDesc(userId, profile.id());

        if (notifications.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 알림 보낸 프로필들의 ID 추출
        List<Long> fromOrgIds = notifications.stream()
                .map(Notification::getFromMemberOrgId)
                .distinct()
                .toList();


        // 3. 소속별 닉네임 한 번에 조회
        Map<Long, String> orgNicknameMap = memberOrgRepository.findAllById(fromOrgIds).stream()
                .collect(Collectors.toMap(
                        MemberOrg::getId,
                        MemberOrg::getNickname
                ));

        // 4. 알림 데이터와 닉네임 맵 매칭

        return notifications.stream()
                .map(notification -> {
                    String fromNickname = orgNicknameMap.getOrDefault(
                            notification.getFromMemberOrgId(), "알 수 없는 사용자"
                    );
                    return new NotificationRespDto(notification, fromNickname);
                })
                .toList();
    }

    // [ 알림 읽음 ]
    @Transactional
    public void markAsRead(Long notificationId, String email)  {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));

        // 본인의 알림이 맞는지
        if(!notification.getMember().getEmail().equals(email)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        notification.markAsRead();
    }

    // [ SseEmitter로 이벤트 전송하는 공통 메서드 ]
    private void sendEventToEmitter(SseEmitter emitter, Long userId, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(userId + "_" + System.currentTimeMillis())
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            // 전송 중 예외 발생 시 Emitter 제거
            emitterRepository.deleteById(userId);
        }
    }
}
