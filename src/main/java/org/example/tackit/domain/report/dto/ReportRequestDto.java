package org.example.tackit.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.tackit.domain.entity.ReportReason;
import org.example.tackit.domain.entity.TargetType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {

  private Long targetId;
  private TargetType targetType;   // POST / COMMENT
  private ReportReason reason;
}
