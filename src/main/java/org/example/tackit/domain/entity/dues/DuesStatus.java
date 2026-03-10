package org.example.tackit.domain.entity.dues;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dues_status",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dues_id", "member_org_id"})
    })
public class DuesStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dues_id", nullable = false)
  private Dues dues;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_org_id", nullable = false)
  private MemberOrg memberOrg;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  private LocalDateTime paidAt; // 납부 일시

  @Builder
  public DuesStatus(Dues dues, MemberOrg memberOrg) {
    this.dues = dues;
    this.memberOrg = memberOrg;
    this.status = PaymentStatus.UNPAID;
  }

  // ==========================================
  // 비즈니스 로직
  // ==========================================

  /**
   * 회비 납부 처리
   */
  public void markAsPaid() {
    this.status = PaymentStatus.PAID;
    this.paidAt = LocalDateTime.now();
  }

  /**
   * 납부 취소 처리
   */
  public void cancelPayment() {
    this.status = PaymentStatus.UNPAID;
    this.paidAt = null;
  }
}