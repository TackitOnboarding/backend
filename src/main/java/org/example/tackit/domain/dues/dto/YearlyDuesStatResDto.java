package org.example.tackit.domain.dues.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YearlyDuesStatResDto {

  private Integer month;
  private Long collectedAmount;
  private Long targetAmount;
}