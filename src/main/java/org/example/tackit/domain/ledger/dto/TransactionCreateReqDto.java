package org.example.tackit.domain.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.ledger.TransactionCategory;
import org.example.tackit.domain.entity.ledger.TransactionType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionCreateReqDto {

  @NotNull(message = "거래 일자는 필수입니다.")
  private LocalDate transactionDate;

  @NotBlank(message = "거래처/가맹점명은 필수입니다.")
  private String storeName;

  @NotNull(message = "거래 금액은 필수입니다.")
  private Long amount;

  @NotNull(message = "거래 카테고리는 필수입니다.")
  private TransactionCategory transactionCategory;

  @NotNull(message = "거래 타입은 필수입니다.")
  private TransactionType transactionType;

  private String description;
}