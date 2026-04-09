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
    String filterType = (type == null) ? "ALL" : type.toUpperCase();

    Page<Object[]> result = reportRepository.findPostsWithLatestReport(filterType, pageable);

    return result.map(tuple -> {
      Post post = (Post) tuple[0];
      Report report = (Report) tuple[1];

      int reportCnt = post.getReportCnt();
      ActiveStatus status = post.getActiveStatus();

      return ReportedPostDto.from(report, reportCnt, status);
    });
  }

  @Override
  public ReportedPostDetailDto getReportedPostDetail(Long reportId) {
    // 1. report 조회
    Report baseReport = reportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));

    Long postId = baseReport.getPostId();

    // 2. 최신 신고 게시글만 조회되도록(중복 게시글 방지)
    Report latestReport = reportRepository.findLatestReportByPostId(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));

    // 3. 게시글 조회
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

    return ReportedPostDetailDto.from(latestReport, post);
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
