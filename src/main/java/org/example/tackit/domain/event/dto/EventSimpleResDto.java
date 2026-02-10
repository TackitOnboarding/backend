package org.example.tackit.domain.event.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSimpleResDto {
    private Long eventId;
    private String title;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String colorChip;
}