package org.example.tackit.domain.notification.repository;

import org.example.tackit.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 사용자 계정 ID(memberId)와 현재 선택한 프로필 ID(memberOrgId)를 모두 만족하는 알림 조회
    List<Notification> findAllByMemberIdAndMemberOrgIdOrderByCreatedAtDesc(Long memberId, Long memberOrgId);
}
