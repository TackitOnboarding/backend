package org.example.tackit.config;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Organization.repository.SchoolRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommonDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolRepository schoolRepository;

    // commandLineRunner의 run 메서드는 String... args로 유지 (String[] args와 유사) -> 스프링 부트 공식 문서 참고
    // args를 꼭 배열로 넘겨야 하는 건 아니고 가변적으로 받을 수 있다는 의도를 나타냄
    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.findByEmail("admin").isEmpty()) {
            Member admin = Member.builder()
                    .email("contact.tackit@gmail.com")
                    .password(passwordEncoder.encode("admin1")) // BCrypt 인코딩
                    .name("관리자")
                    .status(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            memberRepository.save(admin);
        }

        if (schoolRepository.findBySchoolName("숙명여자대학교").isEmpty()) {
            School sookmyung = School.builder()
                    .schoolName("숙명여자대학교")
                    .schoolType(SchoolType.Main)
                    .regionId(1)
                    .address("서울특별시 용산구 청파로47길 100")
                    .build();

            schoolRepository.save(sookmyung);
        }
    }
}
