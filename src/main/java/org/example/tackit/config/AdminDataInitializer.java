package org.example.tackit.config;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // commandLineRunner의 run 메서드는 String... args로 유지 (String[] args와 유사) -> 스프링 부트 공식 문서 참고
    // args를 꼭 배열로 넘겨야 하는 건 아니고 가변적으로 받을 수 있다는 의도를 나타냄
    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.findByEmail("admin").isEmpty()) {
            Member admin = Member.builder()
                    .email("admin")
                    .password(passwordEncoder.encode("admin1")) // BCrypt 인코딩
                    .name("숙명")
                    .nickname("관리자")
                    .organization("ADMIN")
                    .joinedYear(2025)
                    .memberRole(MemberRole.ADMIN)
                    .memberType(MemberType.ADMIN)
                    // .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            memberRepository.save(admin);
        }
    }
}
