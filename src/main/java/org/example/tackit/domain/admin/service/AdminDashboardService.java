package org.example.tackit.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_comment.repository.FreeCommentRepository;
import org.example.tackit.domain.Free_board.Free_post.repository.FreePostJPARepository;
import org.example.tackit.domain.QnA_board.QnA_comment.repository.QnACommentRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostRepository;
import org.example.tackit.domain.admin.repository.UserLogRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.report.dto.ReportContentDetailDto;
import org.example.tackit.domain.report.dto.ReportLogDto;
import org.example.tackit.domain.report.dto.ReportListDto;
import org.example.tackit.domain.report.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {
    private final TipPostRepository tipPostRepository;
    private final QnAPostRepository qnAPostRepository;
    private final FreePostJPARepository freePostJPARepository;

    private static final List<String> EXCLUDED_ACTIONS = Arrays.asList("sign-in", "sign-up", "sign-out");
    private final UserLogRepository userLogRepository;
    private final ReportRepository reportRepository;
    private final FreeCommentRepository freeCommentRepository;
    private final QnACommentRepository qnACommentRepository;

    // [ 총 게시글 수 계산 ]
    public long getPostsCount() {
        long tipPostCount = tipPostRepository.count();
        long qnAPostCount = qnAPostRepository.count();
        long freePostCount = freePostJPARepository.count();

        return tipPostCount + qnAPostCount + freePostCount;
    }

    // [ DAU 계산 ]
    public Long getDau(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return userLogRepository.findDauByTimestampBetween(startOfDay, endOfDay, EXCLUDED_ACTIONS);
    }

    // [ MAU 계산 ]
    public Long getMau(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endTime = lastDayOfMonth.atTime(LocalTime.MAX);

        return userLogRepository.findMauByTimestampBetween(startTime, endTime, EXCLUDED_ACTIONS);
    }

    // [ 신고로 삭제된 게시글 수 계산 ]
    public Long getDeletedPostsByReport() {
        long deletedTips = tipPostRepository.countByStatus(Status.DELETED);
        long deletedFrees = freePostJPARepository.countByStatus(Status.DELETED);
        long deletedQnAs = qnAPostRepository.countByStatus(Status.DELETED);

        return deletedTips + deletedFrees + deletedQnAs;
    }

    // [ 헬퍼 메서드 ]
    private record ReportedContentInfo(String content, Status status, String writer) {
        public static ReportedContentInfo deleted(TargetType targetType) {
            String typeName = targetType.name();
            String content = typeName.endsWith("_POST") ? "삭제된 게시글" : "삭제된 댓글";

            // writer에 기본값 추가
            return new ReportedContentInfo(content, Status.DELETED, "-");
        }
    }

    // [ 신고 사유 전체 조회 ]
    public Page<ReportListDto> findAllReports(Pageable pageable) {
        Page<ReportRepository.ReportedTargetInfo> targetsPage = reportRepository.findReportedTargets(pageable);

        Page<ReportListDto> dtos = targetsPage.map(target -> {
            ReportedContentInfo info = getReportedContentInfo(target.getTargetId(), target.getTargetType());

            return ReportListDto.builder()
                    .targetId(target.getTargetId())
                    .targetType(target.getTargetType())
                    .title(info.content())
                    .status(info.status())
                    .reportCount(target.getReportCount().intValue())
                    .lastReportedAt(target.getLastReportedAt())
                    .build();
        });
        return dtos;
    }

    // [ 제목과 상태를 조회하는 헬퍼 메서드 ]
    private ReportedContentInfo getReportedContentInfo(Long targetId, TargetType targetType) {
        try {
            switch (targetType) {
                case TIP_POST:
                    TipPost tipPost = tipPostRepository.findById(targetId).orElse(null);
                    return (tipPost != null) ?
                            new ReportedContentInfo(tipPost.getTitle(), tipPost.getStatus(), tipPost.getWriter().getNickname()) :
                            ReportedContentInfo.deleted(TargetType.TIP_POST);

                case FREE_POST:
                    FreePost freePost = freePostJPARepository.findById(targetId).orElse(null);
                    return (freePost != null) ?
                            new ReportedContentInfo(freePost.getTitle(), freePost.getStatus(), freePost.getWriter().getNickname()) :
                            ReportedContentInfo.deleted(TargetType.FREE_POST);

                case QNA_POST:
                    QnAPost qnAPost = qnAPostRepository.findById(targetId).orElse(null);
                    return (qnAPost != null) ?
                            new ReportedContentInfo(qnAPost.getTitle(), qnAPost.getStatus(), qnAPost.getWriter().getNickname()) :
                            ReportedContentInfo.deleted(TargetType.QNA_POST);

                case FREE_COMMENT:
                    FreeComment freeComment = freeCommentRepository.findById(targetId).orElse(null);
                    return (freeComment != null) ?
                            new ReportedContentInfo(freeComment.getContent(), Status.ACTIVE, freeComment.getWriter().getNickname()) :
                            ReportedContentInfo.deleted(TargetType.FREE_COMMENT);

                case QNA_COMMENT:
                    QnAComment qnAComment = qnACommentRepository.findById(targetId).orElse(null);
                    return (qnAComment != null) ?
                            new ReportedContentInfo(qnAComment.getContent(), Status.ACTIVE, qnAComment.getWriter().getNickname()) :
                            ReportedContentInfo.deleted(TargetType.QNA_COMMENT);
                    default:
                        return new ReportedContentInfo("알 수 없는 타입", Status.DELETED, "알 수 없음");
            }
        } catch (Exception e) {
            return new ReportedContentInfo("데이터 조회 오류", Status.DELETED, "알 수 없음");
        }
    }

    // 상세 신고 게시글 및 신고 사유 조회
    public ReportContentDetailDto findReportDetails(Long targetId, TargetType targetType) {
        // 특정 대상의 모든 Report 조회
        List<Report> reports = reportRepository.findReportsByTarget(targetId, targetType);

        List<ReportLogDto> reportLogs = reports.stream()
                .map(ReportLogDto::from)
                .collect(Collectors.toList());

        ReportedContentInfo contentInfo = getReportedContentInfo(targetId, targetType);

        // 신고된 게시판 카테고리 추출
        String postType = extractPostCategory(targetType);

        return ReportContentDetailDto.builder()
                .targetId(targetId)
                .targetType(targetType)
                .postType(postType)
                .contentTitle(contentInfo.content())
                .contentWriter(contentInfo.writer())
                .status(contentInfo.status())
                .reportLogs(reportLogs)
                .build();
    }

    // [ 게시판 이름 추출 헬퍼 메서드 ]
    private String extractPostCategory(TargetType targetType) {
        String name = targetType.name();
        if(name.startsWith("TIP_")) return "TIP";
        if(name.startsWith("QNA_")) return "QNA";
        if(name.startsWith("FREE_")) return "FREE";

        return "UNKNOWN";
    }
}
