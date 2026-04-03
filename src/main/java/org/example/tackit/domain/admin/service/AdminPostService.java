package org.example.tackit.domain.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.ReportedPostDetailDto;
import org.example.tackit.domain.admin.dto.ReportedPostDto;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.post.repository.PostRepository;
import org.example.tackit.domain.report.repository.ReportRepository;
import org.example.tackit.global.exception.BusinessException;
import org.example.tackit.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminPostService implements ReportedPostService {

  private final PostRepository postRepository;
  private final ReportRepository reportRepository;

  @Override
  public Page<ReportedPostDto> getReportedPosts(String type, Pageable pageable) {
    Page<Report> reports;
    String filterType = (type == null) ? "ALL" : type.toUpperCase();

    reports = switch (filterType) {
      case "PENDING" -> reportRepository.findPendingReports(pageable);
      case "DELETED" -> reportRepository.findDeletedReports(pageable);
      default ->        reportRepository.findAllLatestReports(pageable);
    };

    return reports.map(report -> {
      // 현재 게시글의 실시간 상태와 신고 횟수를 가져옴
      Post post = postRepository.findById(report.getPostId()).orElse(null);
      int currentCnt = (post != null) ? post.getReportCnt() : 0;
      ActiveStatus currentStatus = (post != null) ? post.getActiveStatus() : report.getActiveStatus();

      return ReportedPostDto.from(report, currentCnt, currentStatus);
    });
  }

  // 비활성화 게시글 전체 조회
  @Override
  public Page<ReportedPostDto> getDeletedPosts(Pageable pageable) {
    return getReportedPosts("DELETED", pageable);
    /*

    return postRepository.findAllByActiveStatusAndReportCntGreaterThanEqual(
            ActiveStatus.DELETED, 3, pageable)
        .map(ReportedPostDTO::from);
     */
  }

  @Override
  public ReportedPostDetailDto getReportedPostDetail(Long reportId) {
    // 1. 신고 내역 조회 및 예외 처리
    Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));

    // 2. 원본 게시글 조회 및 예외 처리
    Post post = postRepository.findById(report.getPostId())
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    return ReportedPostDetailDto.from(report, post);
  }


  // 신고 게시글 완전 삭제
  @Override
  @Transactional
  public void deletePost(Long id) {
    Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    postRepository.delete(post);
  }

  // 신고 게시글 활성 전환
  @Override
  @Transactional
  public void activatePost(Long id) {
    Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    // 이미 활성 상태인지 체크하는 비즈니스 로직 예시
    if (post.getActiveStatus() == ActiveStatus.ACTIVE) {
      throw new BusinessException(ErrorCode.ALREADY_ACTIVE_POST);
    }

    post.activate();
  }
}
