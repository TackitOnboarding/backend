package org.example.tackit.domain.event.dto;

import lombok.*;
import org.example.tackit.domain.entity.EventScope;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventUpdateReqDto {
    private String title;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String description;
    private EventScope eventScope;
    private List<Long> participants;
    private String colorChip;
}