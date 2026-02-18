package org.example.tackit.domain.poll.repository;

import org.example.tackit.domain.entity.poll.PollTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollTargetRepository extends JpaRepository<PollTarget, Long> {

  boolean existsByPollIdAndMemberOrgId(Long pollId, Long memberOrgId);

  int countByPollId(Long pollId);
}