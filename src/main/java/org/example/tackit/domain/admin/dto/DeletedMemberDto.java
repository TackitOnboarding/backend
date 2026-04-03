package org.example.tackit.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.ActiveStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DeletedMemberDto {
    private Long id;
    private String email;
    private ActiveStatus activeStatus;
    private LocalDateTime createdAt;

    public static DeletedMemberDto from(Member member) {
        return DeletedMemberDto.builder()
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .activeStatus(member.getActiveStatus())
                .build();
    }
}
