package org.example.tackit.domain.poll.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.poll.PollType;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PollDetailResDto {

  private String title;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime endsAt;

  @JsonProperty("isEnded")
  private boolean ended;

  private boolean canVote;
  private PollType optionType;

  private List<PollOptionDto> options;

  @JsonProperty("isVoted")
  private boolean voted; // 내 투표 여부

  private List<Long> myVoteOptionIds; // 내가 투표한 옵션 ID 리스트

  @JsonProperty("isMulti")
  private boolean multi;

  @JsonProperty("isAnonymous")
  private boolean anonymous;

  private int participationCount; // 현재 참여 인원
  private int targetMemberCount;  // 전체 대상 인원

  // 익명 투표일 경우 null 반환
  private List<SimpleMemberProfileDto> voters;

  @Getter
  @Builder
  public static class PollOptionDto {

    private Long id;
    private String content;
    private int voteCount; // 득표 수
  }
}