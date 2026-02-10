package org.example.tackit.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDetailResDto {

    private Long eventId;
    private String title;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String description;
    private String colorChip;
    private List<EventParticipantDto> participants;
}