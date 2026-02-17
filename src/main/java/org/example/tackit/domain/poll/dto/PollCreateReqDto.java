package org.example.tackit.domain.poll.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class PollCreateReqDto {

  @NotNull(message = "조직 ID는 필수입니다.")
  private Long orgId;

  @NotBlank(message = "투표 제목은 필수입니다.")
  private String title;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime endsAt;

  @NotNull(message = "항목 타입은 필수입니다.")
  private PollType optionType; // TEXT or DATE

  @NotEmpty(message = "투표 항목은 최소 1개 이상이어야 합니다.")
  private List<String> options;

  @NotNull(message = "복수 선택 여부는 필수입니다.")
  private Boolean isMulti;

  @NotNull(message = "익명 투표 여부는 필수입니다.")
  private Boolean isAnonymous;

  @NotNull(message = "투표 공개 범위는 필수입니다.")
  private PollScope voteScope; // PARTIAL or ALL

  @NotNull(message = "참여자 목록은 필수입니다. (전체 참여라면 빈 리스트)")
  private List<Long> participants;
}