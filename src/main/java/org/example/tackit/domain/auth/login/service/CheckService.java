package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final MemberRepository memberRepository;

    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    /*
    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

     */
}

