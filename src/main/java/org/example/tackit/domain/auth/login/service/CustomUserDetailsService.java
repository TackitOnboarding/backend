package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.ActiveStatus;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${tackit.admin.email}")
    private String adminEmail;

    public boolean isAdmin(String email) {
        return adminEmail.equals(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));

        // 상태 확인 추가
        if (member.getActiveStatus() == ActiveStatus.DELETED) {
            throw new UsernameNotFoundException(email + " -> 탈퇴한 회원입니다.");
        }

        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (adminEmail.equals(member.getEmail())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                authorities
        );
    }
}
