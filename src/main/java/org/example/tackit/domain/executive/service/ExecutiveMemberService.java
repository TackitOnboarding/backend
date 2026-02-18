package org.example.tackit.domain.executive.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.executive.dto.request.MemberStatusRequest;
import org.example.tackit.domain.executive.dto.response.MemberListResponse;
import org.example.tackit.domain.executive.repository.ExecutiveMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecutiveMemberService {
    private final ExecutiveMemberRepository executiveMemberRepository;
    private final MemberRepository memberRepository;

    // [ 모든 멤버 조회 ]
    @Transactional
    public List<MemberListResponse> getMembers(MemberStatusRequest request) {
        String status = request.getStatus();

        List<Member> members;

        // 상태 조건 없다면 -> 전체
        if( status == null || status.isBlank() )
            members = memberRepository.findAll();

        // 특정 상태(대기, 이용 중, 탈퇴) 조회
        else {

            // members = memberRepository.findByStatus(status);
        }

        return members.stream()
                .map()
    }

    /*
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
        List<Member> deletedMembers = adminMemberRepository.findByStatus(AccountStatus.DELETED);

        List<DeletedMemberDTO> deletedMemberDTOS = deletedMembers.stream()
                .map(member -> DeletedMemberDTO.from(member))
                .collect(Collectors.toList());

        Long deletedCount = (long) deletedMembers.size();

        return new DeletedMemberResp(deletedMemberDTOS, deletedCount);
    }

     */

}
