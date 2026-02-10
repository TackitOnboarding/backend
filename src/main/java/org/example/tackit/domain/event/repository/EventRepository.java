package org.example.tackit.domain.event.repository;

import org.example.tackit.domain.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    // 지정한 날짜 사이의 일정 조회
    @Query("SELECT e FROM Event e " +
            "WHERE e.organization.id = :orgId " +  // 조직 ID 조건 추가
            "AND e.startsAt <= :endDateTime AND e.endsAt >= :startDateTime " +
            "ORDER BY e.startsAt ASC")
    List<Event> findAllByOrganizationIdAndDateRange(
            @Param("orgId") Long orgId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}