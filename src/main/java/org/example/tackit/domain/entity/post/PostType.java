package org.example.tackit.domain.entity.post;

public enum PostType {
  TIP("선배가 알려줘요"),
  QNA("신입이 질문해요"),
  FREE("다같이 얘기해요"),
  NOTICE("공지사항");

  private final String description;

  PostType(String description) {
    this.description = description;
  }
}