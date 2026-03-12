package org.example.tackit.domain.poll.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.poll.PollScope;
import org.example.tackit.domain.entity.poll.PollType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollUpdateReqDto {

  private String title;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime endsAt;

  private PollType optionType;

  private List<String> options;

  private Boolean isMulti;

  private Boolean isAnonymous;

  private PollScope voteScope;

  private List<Long> participants;
}