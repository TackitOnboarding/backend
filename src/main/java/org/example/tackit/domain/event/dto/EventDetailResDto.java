package org.example.tackit.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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