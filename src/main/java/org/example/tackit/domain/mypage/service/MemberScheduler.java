package org.example.tackit.domain.mypage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.repository.MemberRepository;
import org.example.tackit.domain.entity.MemberType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberScheduler {

    private final MemberRepository memberRepository;

    // 매년 1월 1일 0시 0분 0초에 실행
    @Transactional
    @Scheduled(cron = "0 0 0 1 1 *")
    public void updateSeniorMembers() {
        int currentYear = LocalDate.now().getYear();
        int updatedCount = memberRepository.bulkUpdateType(MemberType.NEWBIE, MemberType.SENIOR, currentYear);
        System.out.println("업데이트된 멤버 수 = " + updatedCount);
    }

}

