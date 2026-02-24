package org.example.tackit.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ActiveStatus;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    // nickname, email, org, status, created_at
    private String nickname;
    private String email;
    private String organization;
    private ActiveStatus activeStatus;
    private LocalDate createdAt;
}
