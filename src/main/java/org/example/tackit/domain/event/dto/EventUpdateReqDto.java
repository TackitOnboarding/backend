package org.example.tackit.domain.event.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ColorChip;
import org.example.tackit.domain.entity.EventScope;

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
  private ColorChip colorChip;
}