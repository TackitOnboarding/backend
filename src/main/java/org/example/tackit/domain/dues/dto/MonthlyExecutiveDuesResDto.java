package org.example.tackit.domain.dues.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyExecutiveDuesResDto {

  private Long duesId;
  private String title;
  private LocalDate startDate;
  private LocalDate endDate;
  private Long targetAmount;
  private Long collectedAmount;
  private Integer totalMemberCount;
  private Integer paidMemberCount;

  private MemberPageDto memberPage;
}