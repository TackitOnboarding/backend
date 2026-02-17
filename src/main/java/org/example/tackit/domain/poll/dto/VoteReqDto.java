package org.example.tackit.domain.poll.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteReqDto {

  @NotNull(message = "옵션 ID 리스트는 필수입니다. (투표 취소 시 빈 리스트 전송)")
  private List<Long> optionIds;
}