package org.example.tackit;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.tackit.domain.admin.repository.AdminMemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.AccountStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberSchedulerTest {

    /*
    @Autowired
    private AdminMemberRepository adminMemberRepository;

    @Autowired
    private EntityManager em; // 영속성 컨텍스트 선언

    @Test
    void testBulkUpdateRole() {
        // given: 2024년에 가입한 NEWBIE 멤버 저장 -> 테스트용 멤버
        Member newbie = Member.builder()
                .email("test@test.com")
                .password("pw")
                .nickname("뉴비")
                .organization("ORG")
                .memberType(MemberType.NEWBIE)
                .joinedYear(2024)
                .status(AccountStatus.ACTIVE)
                .build();
        adminMemberRepository.save(newbie);

        int currentYear = 2025;
        int thresholdYear = currentYear - 1;

        // when: bulk update 실행
        int updatedCount = adminMemberRepository.bulkUpdateType(MemberType.NEWBIE, MemberType.SENIOR, thresholdYear);

        // then: 업데이트된 row 수 확인
        assertThat(updatedCount).isEqualTo(1);

        // 캐시 초기화 후 DB에서 다시 조회 (영속성 컨텍스트)
        em.clear();
        Member updatedMember = adminMemberRepository.findById(newbie.getId()).orElseThrow();

        // role이 SENIOR로 바뀌었는지 확인
        assertThat(updatedMember.getMemberType()).isEqualTo(MemberType.SENIOR);
    }

     */
}

