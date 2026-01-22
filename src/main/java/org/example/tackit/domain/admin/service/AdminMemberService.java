package org.example.tackit.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.DeletedMemberDTO;
import org.example.tackit.domain.admin.dto.DeletedMemberResp;
import org.example.tackit.domain.admin.dto.MemberDTO;
import org.example.tackit.domain.admin.dto.MemberStatisticsDTO;
import org.example.tackit.domain.admin.repository.AdminMemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final AdminMemberRepository adminMemberRepository;

    // [ 모든 멤버 조회 ]
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembersOrderByStatus() {
        return adminMemberRepository.findAllOrderByStatus().stream()
                .map(member -> MemberDTO.builder()
                        .nickname(member.getNickname())
                        .email(member.getEmail())
                        .organization(member.getOrganization())
                        .status(member.getStatus())
                        .createdAt(member.getCreatedAt().toLocalDate())
                        .build())
                .collect(Collectors.toList());
    }

    // [ 총 회원 수, 이번 달 신규 회원 수, 이번 주 신규 회원 수 ]
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public DeletedMemberResp getDeletedMembers() {
        List<Member> deletedMembers = adminMemberRepository.findByStatus(Status.DELETED);

        List<DeletedMemberDTO> deletedMemberDTOS = deletedMembers.stream()
                .map(member -> DeletedMemberDTO.from(member))
                .collect(Collectors.toList());

        Long deletedCount = (long) deletedMembers.size();

        return new DeletedMemberResp(deletedMemberDTOS, deletedCount);
    }


}
