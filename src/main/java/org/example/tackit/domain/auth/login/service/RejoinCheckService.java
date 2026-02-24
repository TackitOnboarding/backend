package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RejoinCheckService {
    private final MemberRepository memberRepository;

    // 회원 상태 조회
    public String checkEmailStatus(String email) {
        if (memberRepository.existsByEmailAndActiveStatus(email, ActiveStatus.DELETED)) {
            return "DELETED";
        } else if (memberRepository.existsByEmailAndActiveStatus(email, ActiveStatus.ACTIVE)) {
            return "ACTIVE";
        } else {
            return "AVAILABLE";
        }
    }

    // 탈퇴한 회원 삭제
    public boolean deleteDeletedMember(String email) {
        Optional<Member> deletedMember = memberRepository.findByEmailAndActiveStatus(email, ActiveStatus.DELETED);
        if (deletedMember.isPresent()) {
            memberRepository.delete(deletedMember.get());
            return true;
        }
        return false;
    }

}
