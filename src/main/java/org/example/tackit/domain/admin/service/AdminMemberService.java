package org.example.tackit.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.MemberDTO;
import org.example.tackit.domain.admin.dto.MemberStatisticsDTO;
import org.example.tackit.domain.admin.repository.AdminMemberRepository;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {
    private final AdminMemberRepository adminMemberRepository;

    // [ 모든 멤버 조회 ]
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        return adminMemberRepository.findAllByActiveStatus(ActiveStatus.ACTIVE, pageable)
                .map(this::convertToDTO);
    }

    // [ 총 회원 수, 이번 달 신규 회원 수, 이번 주 신규 회원 수 ]
    public MemberStatisticsDTO getMemberStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime startOfWeek = now.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atStartOfDay();

        long totalCount = adminMemberRepository.countAll();
        long monthlyCount = adminMemberRepository.countJoinedAfter(startOfMonth);
        long weeklyCount = adminMemberRepository.countJoinedAfter(startOfWeek);

        return new MemberStatisticsDTO(totalCount, monthlyCount, weeklyCount);
    }

    // [ 탈퇴 회원 수 조회 ]
    public Page<MemberDTO> getDeletedMembers(Pageable pageable) {
        return adminMemberRepository.findAllByActiveStatus(ActiveStatus.DELETED, pageable)
                .map(this::convertToDTO);
    }

    // DTO 변환 공통 로직
    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .name(member.getName())
                .email(member.getEmail())
                .activeStatus(member.getActiveStatus())
                .createdAt(member.getCreatedAt().toLocalDate())
                .build();
    }


}
