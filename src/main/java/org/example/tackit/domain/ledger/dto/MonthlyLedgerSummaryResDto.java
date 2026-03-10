package org.example.tackit.domain.ledger.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.ledger.TransactionCategory;

@Getter
@Builder
public class MonthlyLedgerSummaryResDto {

  private Long totalIncome;
  private Long totalExpense;
  private Long balance;
  private List<CategoryStatusDto> categoryStatus;

  @Getter
  @Builder
  public static class CategoryStatusDto {

    private TransactionCategory category;
    private Long amount;
  }
}