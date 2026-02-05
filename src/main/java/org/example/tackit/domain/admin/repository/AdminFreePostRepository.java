package org.example.tackit.domain.admin.repository;

import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdminFreePostRepository extends JpaRepository<FreePost, Long> {

    Page<FreePost> findAllByAccountStatusAndReportCountGreaterThanEqual(AccountStatus accountStatus, int reportCount, Pageable pageable);




}
