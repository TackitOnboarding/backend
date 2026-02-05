package org.example.tackit.domain.admin.repository;

import org.example.tackit.domain.entity.MemberLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberLogRepository extends JpaRepository<MemberLog, Long> {

    /*

    @Query("SELECT COUNT(DISTINCT ml.member) " +
            "FROM MemberLog ml " +
            "WHERE ml.action IS NOT NULL " +
            "AND ml.action NOT IN :excludedActions " +
            "AND ml.member <> 'admin' " +
            "AND ml.timestamp BETWEEN  :startOfDay AND :endOfDay")
    Long findDauByTimestampBetween(
            @Param("startOfDay")LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("excludedActions") List<String> excludedActions
            );

    // 31일의 데이터까지 포함하기 위해 startTime, endTime으로 정의
    @Query("SELECT COUNT(DISTINCT ml.member) " +
            "FROM MemberLog ml " +
            "WHERE ml.action IS NOT NULL " +
            "AND ml.action NOT IN :excludedActions " +
            "AND ml.member <> 'admin' " +
            "AND ml.timestamp BETWEEN :startTime AND :endTime")
    Long findMauByTimestampBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludedActions") List<String> excludedActions
    );

     */

}
