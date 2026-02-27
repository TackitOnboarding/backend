package org.example.tackit.domain.event.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ColorChip;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSimpleResDto {

  private Long eventId;
  private String title;
  private LocalDateTime startsAt;
  private LocalDateTime endsAt;
  private ColorChip colorChip;
}