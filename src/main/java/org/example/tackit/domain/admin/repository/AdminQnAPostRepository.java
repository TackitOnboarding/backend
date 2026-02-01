package org.example.tackit.domain.admin.repository;

import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminQnAPostRepository extends JpaRepository<QnAPost, Long> {

    Page<QnAPost> findAllByAccountStatusAndReportCountGreaterThanEqual(AccountStatus accountStatus, int reportCount, Pageable pageable);
}
