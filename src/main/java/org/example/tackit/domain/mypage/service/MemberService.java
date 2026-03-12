package org.example.tackit.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
@RequiredArgsConstructor
@Service
public class MemberService {
    private final AdminMemberRepository adminMemberRepository;

    // 멤버 객체에게 응답 생성 책임 위임
    public MemberMypageResponse getMyPageInfo(String email) {
        System.out.println("입력된 이메일: " + email);
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));
        return member.generateMypageResponse();
    }

}

 */
