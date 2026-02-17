package org.example.tackit.domain.poll.repository;

import java.util.List;
import java.util.Optional;
import org.example.tackit.domain.entity.poll.PollParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollParticipantRepository extends JpaRepository<PollParticipant, Long> {

  Optional<PollParticipant> findByPollIdAndMemberOrgId(Long pollId, Long memberOrgId);

  boolean existsByPollIdAndMemberOrgId(Long pollId, Long memberOrgId);

  // 해당 투표의 참여자 수
  int countByPollId(Long pollId);

  // 참여자 목록 조회
  List<PollParticipant> findAllByPollId(Long pollId);
}