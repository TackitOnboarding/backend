package org.example.tackit.domain.dues.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.dues.PaymentStatus;

@Getter
@Builder
public class MemberDuesStatusDto {

  private Long memberOrgId;
  private String nickname;
  private String roleTag;
  private String profileImageUrl;
  private PaymentStatus paymentStatus;
}