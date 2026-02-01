package org.example.tackit.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.AccountStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DeletedMemberDTO {
    private Long id;

    // 이메일
    private String email;

    // 닉네임
    private String nickname;

    // 상태
    private AccountStatus accountStatus;

    // 가입일자
    private LocalDateTime createdAt;

    public static DeletedMemberDTO from(Member member) {
        return DeletedMemberDTO.builder()
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .accountStatus(member.getAccountStatus())
                .build();
    }
}
