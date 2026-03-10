package org.example.tackit.domain.entity.ledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.dues.Dues;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ledger_id", nullable = false)
  private Ledger ledger;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dues_id")
  private Dues dues;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionCategory transactionCategory;

  @Column(nullable = false)
  private Long amount;

  @Column(nullable = false)
  private String description;

  private String storeName;

  private String receiptImageUrl;

  @Column(nullable = false)
  private LocalDateTime transactionDate;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Builder
  public Transaction(Ledger ledger, Dues dues, TransactionType transactionType,
      TransactionCategory transactionCategory, Long amount,
      String description, String storeName, String receiptImageUrl,
      LocalDateTime transactionDate) {
    this.ledger = ledger;
    this.dues = dues;
    this.transactionType = transactionType;
    this.transactionCategory = transactionCategory;
    this.amount = amount;
    this.description = description;
    this.storeName = storeName;
    this.receiptImageUrl = receiptImageUrl;
    this.transactionDate = transactionDate;
  }

  // ==========================================
  // 비즈니스 로직
  // ==========================================

  /**
   * 회비 납부 시 마스터 레코드 금액 관리용
   */
  public void addAmount(Long addedAmount) {
    this.amount += addedAmount;
  }

  /**
   * 회비 납부 취소 시 마스터 레코드 금액 관리용
   */
  public void subtractAmount(Long subtractedAmount) {
    if (this.amount >= subtractedAmount) {
      this.amount -= subtractedAmount;
    } else {
      throw new IllegalStateException("차감할 금액이 현재 잔액보다 큽니다.");
    }
  }
}