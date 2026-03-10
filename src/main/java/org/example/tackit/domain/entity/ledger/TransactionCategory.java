package org.example.tackit.domain.entity.ledger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionCategory {
  FOOD("식비"),
  STUFF("사무용품"),
  EVENT("행사"),
  DUES("회비"),
  ETC("기타");

  private final String description;
}