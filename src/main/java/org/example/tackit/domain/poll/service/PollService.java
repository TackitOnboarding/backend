package org.example.tackit.domain.poll.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.org.OrgStatus;
import org.example.tackit.domain.entity.poll.Poll;
import org.example.tackit.domain.entity.poll.PollOption;
import org.example.tackit.domain.entity.poll.PollParticipant;
import org.example.tackit.domain.entity.poll.PollScope;
import org.example.tackit.domain.entity.poll.PollStatus;
import org.example.tackit.domain.entity.poll.PollTarget;
import org.example.tackit.domain.entity.poll.Vote;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.memberOrg.dto.SimpleMemberProfileDto;
import org.example.tackit.domain.memberOrg.repository.MemberOrgRepository;
import org.example.tackit.domain.poll.dto.PollCreateReqDto;
import org.example.tackit.domain.poll.dto.PollDetailResDto;
import org.example.tackit.domain.poll.dto.PollSidebarResDto;
import org.example.tackit.domain.poll.dto.PollSimpleResDto;
import org.example.tackit.domain.poll.dto.PollUpdateReqDto;
import org.example.tackit.domain.poll.dto.VoteReqDto;
import org.example.tackit.domain.poll.repository.PollOptionRepository;
import org.example.tackit.domain.poll.repository.PollParticipantRepository;
import org.example.tackit.domain.poll.repository.PollRepository;
import org.example.tackit.domain.poll.repository.PollTargetRepository;
import org.example.tackit.domain.poll.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PollService {

  private final PollRepository pollRepository;
  private final PollOptionRepository pollOptionRepository;
  private final PollParticipantRepository pollParticipantRepository;
  private final PollTargetRepository pollTargetRepository;
  private final VoteRepository voteRepository;

  private final MemberOrgRepository memberOrgRepository;
  private final MemberOrgValidator memberOrgValidator;

  // 투표 생성
  @Transactional
  public Long createPoll(Long memberOrgId, PollCreateReqDto reqDto) {
    MemberOrg requester = memberOrgValidator.validateExecutive(reqDto.getOrgId(), memberOrgId);

    if (reqDto.getVoteScope() == PollScope.PARTIAL && (reqDto.getParticipants() == null
        || reqDto.getParticipants().isEmpty())) {
      throw new IllegalArgumentException("일부 참여 투표는 대상자가 필수입니다.");
    }

    // 투표 저장
    Poll poll = Poll.builder()
        .orgId(reqDto.getOrgId())
        .creator(requester.getMember())
        .title(reqDto.getTitle())
        .endsAt(reqDto.getEndsAt())
        .pollType(reqDto.getOptionType())
        .scope(reqDto.getVoteScope())
        .isMulti(reqDto.getIsMulti())
        .isAnonymous(reqDto.getIsAnonymous())
        .build();

    pollRepository.save(poll);

    // 옵션 저장
    int seq = 1;
    for (String content : reqDto.getOptions()) {
      pollOptionRepository.save(PollOption.builder()
          .poll(poll)
          .content(content)
          .seq(seq++)
          .build());
    }

    // 투표 참여 대상 저장
    if (reqDto.getVoteScope() == PollScope.PARTIAL) {
      List<Long> targets = reqDto.getParticipants().stream().distinct().toList();

      List<MemberOrg> memberOrgs = memberOrgRepository.findAllById(targets);

      // 개수 검증
      if (memberOrgs.size() != targets.size()) {
        throw new IllegalArgumentException("존재하지 않는 부원 ID가 포함되어 있습니다.");
      }

      for (MemberOrg memberOrg : memberOrgs) {
        // 소속 일치 여부 검증
        if (!memberOrg.getOrganization().getId().equals(poll.getOrgId())) {
          throw new IllegalArgumentException("해당 그룹의 소속 회원이 아닙니다.");
        }
      }

      for (Long targetId : targets) {
        pollTargetRepository.save(PollTarget.builder()
            .poll(poll)
            .memberOrgId(targetId)
            .build());
      }
    }

    return poll.getId();
  }

  // 투표 수정
  @Transactional
  public void updatePoll(Long pollId, Long memberOrgId, PollUpdateReqDto reqDto) {
    Poll poll = findPollOrThrow(pollId);

    memberOrgValidator.validateExecutive(poll.getOrgId(), memberOrgId);

    poll.update(reqDto.getTitle(), reqDto.getEndsAt(), reqDto.getIsMulti());
  }

  // 투표 삭제
  @Transactional
  public void deletePoll(Long pollId, Long memberOrgId) {
    Poll poll = findPollOrThrow(pollId);

    memberOrgValidator.validateExecutive(poll.getOrgId(), memberOrgId);

    pollRepository.delete(poll);
  }

  // 투표 상세 조회
  public PollDetailResDto getPollDetail(Long pollId, Long memberOrgId) {
    Poll poll = findPollOrThrow(pollId);
    memberOrgValidator.validateActiveMembership(poll.getOrgId(), memberOrgId);

    // 내 투표 여부 및 선택 항목 조회
    Optional<PollParticipant> myParticipant = pollParticipantRepository.findByPollIdAndMemberOrgId(
        pollId, memberOrgId);
    boolean isVoted = myParticipant.isPresent();
    List<Long> myVoteOptionIds = isVoted ?
        voteRepository.findOptionIdsByParticipantId(myParticipant.get().getId())
        : Collections.emptyList();

    // 전체 대상자 수 계산
    int targetMemberCount = getTargetMemberCount(poll);

    // 옵션별 득표수 TODO redis 쓰면 좋을듯
    Map<Long, Long> voteCountMap = voteRepository.countByPollIdGroupByOptionId(pollId).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

    List<PollDetailResDto.PollOptionDto> optionDtos = pollOptionRepository.findAllByPollIdOrderBySeqAsc(
            pollId).stream()
        .map(opt -> PollDetailResDto.PollOptionDto.builder()
            .id(opt.getId())
            .content(opt.getContent())
            .voteCount(voteCountMap.getOrDefault(opt.getId(), 0L).intValue())
            .build())
        .toList();

    // 참여자 목록
    List<SimpleMemberProfileDto> voters = null;
    if (!poll.isAnonymous()) {
      List<PollParticipant> participants = pollParticipantRepository.findAllByPollId(pollId);
      if (!participants.isEmpty()) {
        List<Long> voterIds = participants.stream().map(PollParticipant::getMemberOrgId).toList();
        Map<Long, MemberOrg> memberMap = memberOrgRepository.findAllById(voterIds).stream()
            .collect(Collectors.toMap(MemberOrg::getId, Function.identity()));

        voters = participants.stream()
            .map(p -> SimpleMemberProfileDto.from(memberMap.get(p.getMemberOrgId())))
            .filter(Objects::nonNull)
            .sorted()
            .toList();
      }
    }

    return PollDetailResDto.builder()
        .title(poll.getTitle())
        .endsAt(poll.getEndsAt())
        .ended(poll.getStatus() == PollStatus.ENDED)
        .canVote(isCanVote(poll, memberOrgId))
        .optionType(poll.getPollType())
        .options(optionDtos)
        .voted(isVoted)
        .myVoteOptionIds(myVoteOptionIds)
        .multi(poll.isMulti())
        .anonymous(poll.isAnonymous())
        .participationCount(pollParticipantRepository.countByPollId(pollId))
        .targetMemberCount(targetMemberCount)
        .voters(voters)
        .build();
  }

  // 투표하기 (재투표, 투표 취소 포함)
  @Transactional
  public void vote(Long pollId, Long memberOrgId, VoteReqDto reqDto) {
    Poll poll = findPollOrThrow(pollId);

    memberOrgValidator.validateActiveMembership(poll.getOrgId(), memberOrgId);

    // 마감 여부 체크
    if (isPollEnded(poll)) {
      throw new IllegalStateException("이미 마감된 투표입니다.");
    }

    // PARTIAL 범위 체크
    if (!isTargetMember(poll, memberOrgId)) {
      throw new IllegalArgumentException("투표 권한이 없습니다.");
    }

    // 투표 취소 시 삭제
    if (reqDto.getOptionIds().isEmpty()) {
      pollParticipantRepository.findByPollIdAndMemberOrgId(pollId, memberOrgId)
          .ifPresent(pollParticipantRepository::delete);
      return;
    }

    // 복수 선택 체크
    if (!poll.isMulti() && reqDto.getOptionIds().size() > 1) {
      throw new IllegalArgumentException("복수 선택이 불가능합니다.");
    }

    // 참여자 정보 조회(재투표) or 생성(첫 투표)
    PollParticipant participant = pollParticipantRepository.findByPollIdAndMemberOrgId(pollId,
            memberOrgId)
        .orElseGet(() -> PollParticipant.builder()
            .poll(poll)
            .memberOrgId(memberOrgId)
            .build());

    if (participant.getId() == null) {
      pollParticipantRepository.save(participant);
    } else {
      voteRepository.deleteByParticipantId(participant.getId());
      participant.reVote();
    }

    // 새 투표 저장
    List<PollOption> options = pollOptionRepository.findAllById(reqDto.getOptionIds());
    if (options.size() != reqDto.getOptionIds().size()) {
      throw new IllegalArgumentException("유효하지 않은 옵션이 포함되어 있습니다.");
    }

    for (PollOption option : options) {
      voteRepository.save(Vote.builder()
          .participant(participant)
          .pollOption(option)
          .build());
    }
  }

  // 월간 조회
  public List<PollSimpleResDto> getMonthlyPolls(Long orgId, int year, int month, Long memberOrgId) {
    memberOrgValidator.validateActiveMembership(orgId, memberOrgId);

    LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
    LocalDateTime end = start.plusMonths(1).minusNanos(1);

    return pollRepository.findAllByOrgIdAndEndsAtBetweenOrderByEndsAtAsc(orgId, start, end).stream()
        .map(p -> PollSimpleResDto.builder()
            .id(p.getId())
            .title(p.getTitle())
            .endsAt(p.getEndsAt())
            .build())
        .toList();
  }

  // 사이드바 조회 (마감 임박 + 진행 중)
  public PollSidebarResDto getSidebarPolls(Long orgId, Long memberOrgId) {
    memberOrgValidator.validateActiveMembership(orgId, memberOrgId);

    // 진행 중인 모든 투표 조회
    List<Poll> activePolls = pollRepository.findAllByOrgIdAndStatusOrderByEndsAtAsc(orgId,
        PollStatus.ONGOING);

    List<PollSidebarResDto.PollSummaryDto> urgentList = new ArrayList<>();
    List<PollSidebarResDto.PollSummaryDto> normalList = new ArrayList<>();

    // 마감 임박 기준 시간 설정 (3일)
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime urgentThreshold = now.plusDays(3);

    for (Poll poll : activePolls) {
      // 투표 대상자가 아니면 노출 안함
      if (!isTargetMember(poll, memberOrgId)) {
        continue;
      }

      // 마감된 건 노출 안함
      if (isPollEnded(poll)) {
        continue;
      }

      boolean isVoted = pollParticipantRepository.existsByPollIdAndMemberOrgId(poll.getId(),
          memberOrgId);
      int participationCount = pollParticipantRepository.countByPollId(poll.getId());
      int targetMemberCount = getTargetMemberCount(poll);

      PollSidebarResDto.PollSummaryDto summary = PollSidebarResDto.PollSummaryDto.builder()
          .id(poll.getId())
          .title(poll.getTitle())
          .endsAt(poll.getEndsAt())
          .participationCount(participationCount)
          .targetMemberCount(targetMemberCount)
          .isVoted(isVoted)
          .build();

      // 마감 임박 여부 확인
      boolean isUrgent = !isVoted
          && poll.getEndsAt() != null
          && poll.getEndsAt().isBefore(urgentThreshold);

      if (isUrgent) {
        urgentList.add(summary);
      } else {
        normalList.add(summary);
      }
    }

    return PollSidebarResDto.builder()
        .urgentVotes(urgentList)
        .votes(normalList)
        .build();
  }


  // 투표 존재 확인 메서드
  private Poll findPollOrThrow(Long pollId) {
    return pollRepository.findById(pollId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
  }

  // 전체 투표 대상자 수 계산
  private int getTargetMemberCount(Poll poll) {
    if (poll.getScope() == PollScope.ALL) {
      return memberOrgRepository.countByOrganizationIdAndOrgStatus(
          poll.getOrgId(), OrgStatus.ACTIVE
      );
    } else {
      return pollTargetRepository.countByPollId(poll.getId());
    }
  }

  // 투표 대상자 여부 확인
  private boolean isTargetMember(Poll poll, Long memberOrgId) {
    if (poll.getScope() == PollScope.ALL) {
      return true;
    } else {
      return pollTargetRepository.existsByPollIdAndMemberOrgId(poll.getId(), memberOrgId);
    }
  }

  // 투표 마감 여부 확인
  private boolean isPollEnded(Poll poll) {
    if (poll.getStatus() == PollStatus.ENDED) {
      return true;
    }
    return poll.getEndsAt() != null && poll.getEndsAt().isBefore(LocalDateTime.now());
  }

  // 현재 투표 가능 여부 확인
  private boolean isCanVote(Poll poll, Long memberOrgId) {
    // 마감된 투표는 투표 불가
    if (isPollEnded(poll)) {
      return false;
    }

    // 마감되지 않았을 경우 대상자 여부 확인
    return isTargetMember(poll, memberOrgId);
  }
}