package org.example.tackit.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.tackit.domain.entity.ReportReason;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportReqDto {

  private ReportReason reportReason;
  private String detailReason;
}
