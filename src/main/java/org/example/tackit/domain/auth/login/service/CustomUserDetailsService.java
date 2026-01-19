package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.auth.login.repository.UserRepository;
import org.example.tackit.domain.entity.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));

        // 상태 확인 추가
        if (member.getStatus() == Status.DELETED) {
            throw new UsernameNotFoundException(email + " -> 탈퇴한 회원입니다.");
        }

        return createUserDetails(member);
    }

    // DB에 Member 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Member member) {
        // 1. 여러 권한을 담을 리스트 생성
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 2. memberRole 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getMemberRole().name()));

        // 3. memberType 추가
        authorities.add((new SimpleGrantedAuthority(member.getMemberType().name())));

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getOrganization(),
                authorities
        );
    }
}
