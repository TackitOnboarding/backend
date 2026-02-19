package org.example.tackit.domain.report.repository;

import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.entity.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 전체/필터링 조회 (Fetch Join으로 신고자와 작성자 정보를 한 번에 가져옴)
    @Query("select r from Report r " +
            "join fetch r.reporter " +
            "join fetch r.writer " +
            "where (:status is null or r.activeStatus = :status)")
    Page<Report> findAllByActiveStatus(@Param("status")ActiveStatus activeStatus, Pageable pageable);

    // 필요하다면 신고 중복 방지를 위한 조회 메서드도 추가 가능
    boolean existsByReporterIdAndTargetIdAndTargetType(Long reporterId, Long targetId, TargetType targetType);

    interface ReportedTargetInfo {
        Long getTargetId();
        TargetType getTargetType();
        Long getReportCount();
        LocalDateTime getLastReportedAt();
    }
    // 신고된 컨텐츠 그룹화하는 쿼리
    @Query("SELECT r.targetId as targetId, r.targetType as targetType, " +
           "        COUNT(r) as reportCount, MAX(r.reportedAt) as lastReportedAt " +
           "FROM Report r " +
            "GROUP BY r.targetId, r.targetType")
    Page<ReportedTargetInfo> findReportedTargets(Pageable pageable);


    // 특정 신고물의 모든 신고 내역 조회
    @Query("SELECT r FROM Report r JOIN FETCH r.reporter WHERE r.targetId = :targetId AND r.targetType = :targetType")
    List<Report> findReportsByTarget(
            @Param("targetId") Long targetId,
            @Param("targetType") TargetType targetType
    );

}

