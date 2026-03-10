package org.example.tackit.domain.ledger.dto;

public record TransactionIdResDto(Long transactionId) {

  public static TransactionIdResDto from(Long transactionId) {
    return new TransactionIdResDto(transactionId);
  }
}