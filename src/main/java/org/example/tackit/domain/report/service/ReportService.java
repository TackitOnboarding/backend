package org.example.tackit.domain.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.report.dto.ReportRequestDto;
import org.example.tackit.domain.report.repository.ReportRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createReport(ReportRequestDto dto, String email, String org) {
        Member reporter = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Report report = Report.fromDto(dto, reporter);

        reportRepository.save(report);
    }
}
