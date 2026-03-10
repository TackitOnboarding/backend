package org.example.tackit.domain.dues.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberPageDto {

  private List<MemberDuesStatusDto> content;
  private int currentPage;
  private int totalPages;
  private long totalElements;
  private boolean hasNext;
}