package org.example.tackit.domain.auth.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
// 전체 멀티 프로필 보여주는 용도
public class SignInResponse {
    // 인증 정보
    private TokenDto token;

    // 멀티 프로필 목록
    private List<MultiProfileDto> profiles;

    // 특정 소속 선택 시
    public static SignInResponse of(TokenDto token) {
        return SignInResponse.builder()
                .token(token)
                .profiles(null) // 이미 선택했으므로 목록 보내지 않음 / 혹은 현재 정보만 담도록
                .build();
    }
}
