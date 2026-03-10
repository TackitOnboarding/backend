package org.example.tackit.domain.ledger.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.ledger.TransactionType;

@Getter
@Builder
public class TransactionDetailResDto {

  private Long transactionId;
  private String storeName;
  private LocalDate transactionDate;
  private TransactionCategoryResDto category;
  private String description;
  private TransactionType transactionType;
  private Long amount;
}