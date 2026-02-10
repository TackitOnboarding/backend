package org.example.tackit.domain.event.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Organization.repository.OrganizationRepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgStatus;
import org.example.tackit.domain.entity.Org.Organization;
import org.example.tackit.domain.event.dto.*;
import org.example.tackit.domain.event.repository.EventRepository;
import org.example.tackit.global.exception.ErrorCode;
import org.example.tackit.global.exception.MemberNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberOrgRepository memberOrgRepository;

    // 이벤트 참가자 추가 메서드
    private void addParticipants(Event event, List<Long> memberOrgIds) {
        if (memberOrgIds == null || memberOrgIds.isEmpty()) return;

        List<MemberOrg> memberOrgs = memberOrgRepository.findAllById(memberOrgIds);

        // 개수 검증
        if (memberOrgs.size() != memberOrgIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 부원 ID가 포함되어 있습니다.");
        }

        for (MemberOrg memberOrg : memberOrgs) {
            // 소속 동아리 일치 여부 검증
            if (!memberOrg.getOrganization().getId().equals(event.getOrganization().getId())) {
                throw new IllegalArgumentException("해당 동아리의 소속 부원이 아닙니다.");
            }

            // 참여자 생성
            EventParticipant participant = EventParticipant.builder()
                    .event(event)
                    .memberOrg(memberOrg)
                    .build();

            participant.assignEvent(event);
        }
    }

    // 이벤트 존재 확인 메서드
    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
    }

    // 활동 중인 멤버(운영진 포함)인지 확인하는 메서드
    private void validateMembership(Long orgId, Long memberId) {
        boolean isActiveMember = memberOrgRepository.existsByMemberIdAndOrganizationIdAndOrgStatus(
                memberId,
                orgId,
                OrgStatus.ACTIVE
        );

        if (!isActiveMember) {
            throw new IllegalArgumentException("해당 조직의 활동 중인 회원만 접근할 수 있습니다.");
        }
    }

    // 활동 중인 운영진인지 확인하는 메서드
    private MemberOrg validateExecutive(Long orgId, Long memberId) {
        MemberOrg memberOrg = memberOrgRepository.findByMemberIdAndOrganizationIdAndOrgStatus(
                memberId,
                orgId,
                OrgStatus.ACTIVE
        ).orElseThrow(() -> new IllegalArgumentException("해당 조직의 활동 중인 회원만 접근할 수 있습니다."));

        if (memberOrg.getMemberRole() != MemberRole.EXECUTIVE) {
            throw new IllegalArgumentException("해당 조직의 운영진만 일정을 관리할 수 있습니다.");
        }

        return memberOrg;
    }
}
