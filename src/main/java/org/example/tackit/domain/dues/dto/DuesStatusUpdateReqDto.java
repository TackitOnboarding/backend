package org.example.tackit.domain.dues.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.dues.PaymentStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuesStatusUpdateReqDto {

  @NotNull(message = "변경할 납부 상태는 필수입니다.")
  private PaymentStatus paymentStatus;
}