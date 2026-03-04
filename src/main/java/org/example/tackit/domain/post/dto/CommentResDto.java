package org.example.tackit.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResDto {

  private Long id;
  private SimpleMemberProfileDto writer;
  private String content;
  private boolean isMine;
  private boolean isDeleted;
  private LocalDateTime createdAt;
  private List<CommentResDto> children; // 대댓글 리스트
}