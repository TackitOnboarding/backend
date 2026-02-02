package org.example.tackit.domain.auth.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tackit.config.Redis.RedisUtil;
import org.example.tackit.config.jwt.TokenProvider;
import org.example.tackit.domain.admin.repository.AdminMemberRepository;
import org.example.tackit.domain.auth.login.dto.*;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.AccountStatus;
import org.example.tackit.domain.entity.Org.MemberOrg;
import org.example.tackit.domain.entity.Org.OrgType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final AdminMemberRepository adminMemberRepository;
    private final MemberOrgRepository memberOrgRepository;

    @Transactional
    public void signup(SignUpDto signUpDto) {
        if (memberRepository.existsByEmail(signUpDto.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member member = Member.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .accountStatus(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    /*
    @Transactional
    public TokenDto signIn(SignInDto signInDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

        try {
            log.info("로그인 시도: {}", signInDto.getEmail());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.info("로그인 성공: {}", authentication.getName());

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
            redisUtil.save(signInDto.getEmail(), tokenDto.getRefreshToken());
            return tokenDto;
        } catch (Exception e) {
            log.error("로그인 실패", e);
            throw e;
        }
    }
     */

    @Transactional
    public SignInResponse signIn(SignInDto signInDto) {
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

        try {
            log.info("로그인 시도: {}", signInDto.getEmail());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.info("로그인 성공: {}", authentication.getName());

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
            redisUtil.save(signInDto.getEmail(), tokenDto.getRefreshToken());

            // 멀티 프로필 목록 조회
            List<MemberOrg> memberOrgs = memberOrgRepository.findAllByMemberEmail(signInDto.getEmail());

            List<MultiProfileDto> profiles = memberOrgs.stream()
                    .map(org -> MultiProfileDto.builder()
                            .memberOrgId(org.getId())
                            .orgName(org.getOrgType() == OrgType.CLUB ? org.getClub().getName() : org.getCommunity().getName())
                            .nickname(org.getNickname())
                            .profileImage(org.getProfileImageUrl())
                            .orgType(org.getOrgType().name())
                            .memberRole(org.getMemberRole().name())
                            .memberType(org.getMemberType().name())
                            .build())
                    .collect(Collectors.toList());

            return new SignInResponse(
                    tokenDto,
                    profiles
            );
        } catch (Exception e) {
            log.error("로그인 실패: {}", signInDto.getEmail(), e);
            throw e;
        }
    }

    // Bearer 제거 및 형식 검증
    public String resolveBearerToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new BadCredentialsException("리프레시 토큰이 누락되었거나 올바르지 않습니다.");
        }
        return refreshToken.substring(7);
    }

    // 토큰 재발급 처리
    @Transactional
    public TokenDto reissue(String bearerToken) {
        String refreshToken = resolveBearerToken(bearerToken);
        return tokenProvider.reissueAccessToken(refreshToken);
    }

    // 특정 소속 선택
    public SignInResponse selectProfile(Long memberOrgId, String email) {
        // 소속 검증
        MemberOrg selectedOrg = memberOrgRepository.findById(memberOrgId)
                .filter(org -> org.getMember().getEmail().equals(email))
                .orElseThrow( () -> new RuntimeException("해당 소속 권한이 없거나 존재하지 않습니다."));

        // 권한 리스트 생성 : Role + Type
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_" + selectedOrg.getMemberRole().name()),
                new SimpleGrantedAuthority("ROLE_" + selectedOrg.getMemberType().name())
        );

        // 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

        // MemberOrgId 포함하여 토큰 생성
        TokenDto orgToken = tokenProvider.generateTokenDtoWithProfile(authentication, memberOrgId);

        return SignInResponse.of(orgToken);
    }

    // 이메일 찾기
    /*
    @Transactional
    public FindEmailRespDto findEmailbyOrgAndNickname(String organization, String name) {
        Optional<Member> memberOptional = memberRepository.findByOrganizationAndName(organization, name);

        Member member = memberOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.")
        );

        return FindEmailRespDto.builder()
                .email(member.getEmail())
                .build();
    }

    // 비밀번호 찾기
    @Transactional
    public ResetTokenDto findPwByIdentity(String name, String organization, String email) {
        try {
            // 1. 정보 일치 확인 및 회원 조회
            log.info("비밀번호 찾기 본인 확인 시도: 이름={}, 소속={}, 이메일={}", name, organization, email);

            Optional<Member> memberOptional = memberRepository.findByNameAndOrganizationAndEmail(name, organization, email);

            // 2. 일치하는 회원이 없을 경우 예외
            Member member = memberOptional.orElseThrow( () -> {
                log.error("본인 확인 실패: 정보 불일치");

                // 404 Not Found
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보가 일치하지 않습니다.");
            });

            log.info("본인 확인 성공: 사용자 이메일={}", member.getEmail());

            // 3. 재설정 전용 일회성 토큰 발급
            String resetToken = tokenProvider.generateResetToken(member.getEmail());

            // 4. Redis에 토큰 저장 (일회용 보장 및 유효성 관리)
            long expirationTimeSeconds = tokenProvider.getResetTokenExpiresIn() / 1000;

            // Key는 "reset:" + 이메일로 설정 -> 일반 Refresh Token과 구분
            redisUtil.save(
                    "reset:" + member.getEmail(),
                    resetToken,
                    expirationTimeSeconds
            );

            // 5. 토큰 반환
            return ResetTokenDto.builder()
                    .grantType("Bearer")
                    .resetToken(resetToken)
                    .expiresIn(expirationTimeSeconds)
                    .build();
        } catch (Exception e) {
            log.error("비밀번호 찾기 처리 중 에러 발생", e);
            throw new RuntimeException("서버 오류가 발생했습니다.", e);
        }
    }

     */

    // 비밀번호 찾기 ) 비밀번호 재설정
    @Transactional
    public void resetPassword(String authorizationHeader, String newPassword) {
        // 1. 토큰 추출 및 형식 검증
        String resetToken = resolveBearerToken(authorizationHeader);

        // 2. JWT 유효성 및 용도 확인
        if( !tokenProvider.validateToken(resetToken) || !tokenProvider.isResetToken(resetToken) ) {
            throw new BadCredentialsException("유효하지 않거나 만료된 재설정 토큰입니다.");
        }

        // 3. 토큰에서 이메일 추출
        String email = tokenProvider.getEmailFromToken(resetToken);

        // 4. Redis에 저장된 토큰과 일치하는지 확인
        String token = redisUtil.getData("reset:" + email);
        if( token == null || !token.equals(resetToken) ) {
            // 이미 사용되었거나 만료된 토큰
            throw new BadCredentialsException("이미 사용되었거나 유효하지 않은 토큰입니다.");
        }

        // 5. 비밀번호 업데이트
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        member.changePassword(encodedNewPassword);

        // 6. Redis에서 토큰 삭제
        redisUtil.delete("reset:" + email);
    }
}

