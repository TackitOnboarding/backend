package org.example.tackit.domain.entity;

public enum ReportReason {
  ADVERTISEMENT("광고 및 홍보성 내용"),
  DUPLICATE("중복 또는 도배성 내용"),
  FALSE_INFO("허위 정보 또는 사실 왜곡"),
  IRRELEVANT("게시판 주제와 관련 없는 내용"),
  ETC("기타");

  private final String description;

  ReportReason(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}

