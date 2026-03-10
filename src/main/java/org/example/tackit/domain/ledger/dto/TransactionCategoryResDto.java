package org.example.tackit.domain.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.ledger.TransactionCategory;

@Getter
@AllArgsConstructor
public class TransactionCategoryResDto {

  private String code;
  private String name;

  public static TransactionCategoryResDto from(TransactionCategory category) {
    return new TransactionCategoryResDto(
        category.name(),
        category.getDescription()
    );
  }
}