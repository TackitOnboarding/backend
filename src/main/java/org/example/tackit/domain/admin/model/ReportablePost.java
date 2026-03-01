package org.example.tackit.domain.admin.model;

import java.time.LocalDateTime;
import org.example.tackit.domain.entity.org.MemberOrg;

public interface ReportablePost {

  Long getId();

  MemberOrg getWriter();

  String getTitle();

  String getContent();

  LocalDateTime getCreatedAt();

  int getReportCount();


  void activate();

}
