package org.example.tackit.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipantDto {
    private Long orgMemberId;
    private String profileImageUrl;
    private String nickname;
}