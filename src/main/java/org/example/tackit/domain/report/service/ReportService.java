package org.example.tackit.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.report.repository.ReportRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;

    /*
    @Transactional
    public void createReport(ReportRequestDto dto, String email, String org) {
        Member reporter = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Report report = Report.fromDto(dto, reporter);

        reportRepository.save(report);
    }

     */
}
