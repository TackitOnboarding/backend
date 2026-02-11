package org.example.tackit.domain.admin.repository;

import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.TipPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminTipPostRepository extends JpaRepository<TipPost, Long> {

    Page<TipPost> findAllByActiveStatusAndReportCountGreaterThanEqual(ActiveStatus activeStatus, int reportCount, Pageable pageable);
}
