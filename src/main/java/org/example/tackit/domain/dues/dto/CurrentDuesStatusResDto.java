package org.example.tackit.domain.dues.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.dues.PaymentStatus;

@Getter
@Builder
public class CurrentDuesStatusResDto {

  private Long duesId;
  private String title;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer participationRate;
  private Long totalCollectedAmount;
  private Long totalTargetAmount;
  private Integer unpaidCount;
  private PaymentStatus myPaymentStatus;
}