package org.example.tackit.domain.auth.logout.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tackit.config.Redis.RedisUtil;
import org.example.tackit.config.jwt.TokenProvider;
import org.example.tackit.domain.auth.logout.repository.LogoutRepository;
import org.example.tackit.domain.entity.Member;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final LogoutRepository logoutRepository;

    @Transactional
    public void withdraw(String token) {
        String email = tokenProvider.getEmailFromToken(token);
        long expiration = tokenProvider.getExpiration(token);

        // 토큰 무효화 - 로그아웃과 동일
        redisUtil.delete(email);
        redisUtil.setBlackList(token, "logout", expiration);

        // 사용자 상태 DELETED 처리
        Member member = logoutRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다."));
        member.deactivate();

        log.info("탈퇴 처리 완료: {}", email);
    }

}
