package org.example.tackit.domain.admin.model;

import org.example.tackit.domain.entity.Org.MemberOrg;

import java.time.LocalDateTime;

public interface ReportablePost {
    Long getId();
    MemberOrg getWriter();
    String getTitle();
    String getContent();
    LocalDateTime getCreatedAt();
    int getReportCount();


    void activate();

}
