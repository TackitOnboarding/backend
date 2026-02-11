package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));

        // 상태 확인 추가
        if (member.getAccountStatus() == AccountStatus.DELETED) {
            throw new UsernameNotFoundException(email + " -> 탈퇴한 회원입니다.");
        }

        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        // 최소 권한 부여
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                authorities
        );
    }
}
