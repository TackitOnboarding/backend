package org.example.tackit.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipantDto {
    private Long orgMemberId;
    private String profileImageUrl;
    private String nickname;
}