package org.example.tackit.domain.dues.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuesCreateReqDto {

  @NotBlank(message = "회비 제목은 필수입니다.")
  private String title;

  @NotNull(message = "1인당 회비 금액은 필수입니다.")
  @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
  private Long amountPerMember;

  @NotNull(message = "납부 시작일은 필수입니다.")
  private LocalDate startDate;

  @NotNull(message = "납부 마감일은 필수입니다.")
  private LocalDate endDate;

  @NotBlank(message = "납부 대상 범위 지정은 필수입니다.")
  private String dueScope;

  private List<Long> targets;
}