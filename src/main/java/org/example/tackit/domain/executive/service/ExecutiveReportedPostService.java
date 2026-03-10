package org.example.tackit.domain.executive.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.executive.dto.response.ReportedPostResDto;
import org.example.tackit.domain.report.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutiveReportedPostService {

  private final ReportRepository reportRepository;

  // 신고 전체 조회 : 게시글만 O
  public Page<ReportedPostResDto> getReportList(String status, Pageable pageable) {
    ActiveStatus filterStatus = null;

      if ("ACTIVE".equalsIgnoreCase(status)) {
          filterStatus = ActiveStatus.ACTIVE;
      } else if ("DELETED".equalsIgnoreCase(status)) {
          filterStatus = ActiveStatus.DELETED;
      }

    Page<Report> reports = reportRepository.findAllByActiveStatus(filterStatus, pageable);

    return reports.map(ReportedPostResDto::from);
  }

  // 신고 상세 조회
  public ReportedPostResDto getReportDetail(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new EntityNotFoundException("해당 신고 내역이 존재하지 않습니다. ID: " + reportId));
    return ReportedPostResDto.from(report);
  }


}
