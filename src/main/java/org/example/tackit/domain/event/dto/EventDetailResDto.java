package org.example.tackit.domain.event.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ColorChip;
import org.example.tackit.domain.member.dto.SimpleMemberProfileDto;

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
  private ColorChip colorChip;
  private List<SimpleMemberProfileDto> participants;
}