package org.example.tackit.domain.admin.service;

import org.example.tackit.domain.admin.dto.ReportedPostDetailDto;
import org.example.tackit.domain.admin.dto.ReportedPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReportedPostService {
    Page<ReportedPostDto> getReportedPosts(String type, Pageable pageable);

    ReportedPostDetailDto getReportedPostDetail(Long reportId);

    Page<ReportedPostDto> getDeletedPosts(Pageable pageable);

    void deletePost(Long id);

    void activatePost(Long id);

}
