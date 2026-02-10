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

    // 일정 생성
    @Transactional
    public Long createEvent(EventCreateReqDto reqDto, Long requesterId) {
        MemberOrg memberOrg = validateExecutive(reqDto.getOrgId(), requesterId);

        Member creator = memberOrg.getMember();

        Organization organization = organizationRepository.findById(reqDto.getOrgId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 조직입니다."));

        Event event = Event.builder()
                .organization(organization)
                .creator(creator)
                .title(reqDto.getTitle())
                .startsAt(reqDto.getStartsAt())
                .endsAt(reqDto.getEndsAt())
                .description(reqDto.getDescription())
                .colorChip(reqDto.getColorChip())
                .eventScope(reqDto.getEventScope())
                .build();

        // 참여자 추가
        addParticipants(event, reqDto.getParticipants());

        return eventRepository.save(event).getId();
    }

    // 일정 수정
    @Transactional
    public void updateEvent(Long eventId, EventUpdateReqDto reqDto, Long requesterId) {
        Event event = findEventOrThrow(eventId);

        validateExecutive(event.getOrganization().getId(), requesterId);

        event.update(
                reqDto.getTitle(),
                reqDto.getStartsAt(),
                reqDto.getEndsAt(),
                reqDto.getDescription(),
                reqDto.getColorChip(),
                reqDto.getEventScope()
        );

        // 참여자 목록 수정 (기존의 데이터 전부 삭제 후 다시 추가)
        if (reqDto.getParticipants() != null) {
            event.clearParticipants();
            addParticipants(event, reqDto.getParticipants());
        }
    }

    // 일정 삭제
    @Transactional
    public void deleteEvent(Long eventId, Long requesterId) {
        Event event = findEventOrThrow(eventId);

        validateExecutive(event.getOrganization().getId(), requesterId);

        eventRepository.delete(event);
    }

    // 월간 일정 조회
    public List<EventSimpleResDto> getMonthlyEvents(Long orgId, int year, int month, Long requesterId) {
        validateMembership(orgId, requesterId);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<Event> events = eventRepository.findAllByOrganizationIdAndDateRange(orgId, startDateTime, endDateTime);

        return events.stream()
                .map(event -> EventSimpleResDto.builder()
                        .eventId(event.getId())
                        .title(event.getTitle())
                        .startsAt(event.getStartsAt())
                        .endsAt(event.getEndsAt())
                        .colorChip(event.getColorChip())
                        .build())
                .collect(Collectors.toList());
    }

    // 일정 상세 조회
    public EventDetailResDto getEventDetail(Long eventId, Long requesterId) {
        Event event = findEventOrThrow(eventId);

        validateMembership(event.getOrganization().getId(), requesterId);

        List<EventParticipantDto> participantDtos = event.getParticipants().stream()
                .map(ep -> EventParticipantDto.builder()
                        .orgMemberId(ep.getMemberOrg().getId())
                        .profileImageUrl(ep.getMemberOrg().getProfileImageUrl())
                        .nickname(ep.getMemberOrg().getNickname())
                        .build())
                .collect(Collectors.toList());

        return EventDetailResDto.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .startsAt(event.getStartsAt())
                .endsAt(event.getEndsAt())
                .description(event.getDescription())
                .colorChip(event.getColorChip())
                .participants(participantDtos)
                .build();
    }

    // 다가오는 일정 조회
    public List<EventSimpleResDto> getUpcomingEvents(Long orgId, Long requesterId) {
        validateMembership(orgId, requesterId);

        List<Event> events = eventRepository.findByOrganizationIdAndStartsAtAfterOrderByStartsAtAsc(
                orgId,
                LocalDateTime.now()
        );

        return events.stream()
                .map(event -> EventSimpleResDto.builder()
                        .eventId(event.getId())
                        .title(event.getTitle())
                        .startsAt(event.getStartsAt())
                        .endsAt(event.getEndsAt())
                        .colorChip(event.getColorChip())
                        .build())
                .collect(Collectors.toList());
    }

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
