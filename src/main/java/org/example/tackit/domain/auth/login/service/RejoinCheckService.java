package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RejoinCheckService {
    private final MemberRepository memberRepository;

    // 회원 상태 조회
    public String checkEmailStatus(String email) {
        if (memberRepository.existsByEmailAndStatus(email, AccountStatus.DELETED)) {
            return "DELETED";
        } else if (memberRepository.existsByEmailAndStatus(email, AccountStatus.ACTIVE)) {
            return "ACTIVE";
        } else {
            return "AVAILABLE";
        }
    }

    // 탈퇴한 회원 삭제
    public boolean deleteDeletedMember(String email) {
        Optional<Member> deletedMember = memberRepository.findByEmailAndStatus(email, AccountStatus.DELETED);
        if (deletedMember.isPresent()) {
            memberRepository.delete(deletedMember.get());
            return true;
        }
        return false;
    }

}
