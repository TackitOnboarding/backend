package org.example.tackit.domain.poll.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.example.tackit.domain.entity.poll.Poll;
import org.example.tackit.domain.entity.poll.PollStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {

  // 월간 투표
  List<Poll> findAllByOrgIdAndEndsAtBetweenOrderByEndsAtAsc(
      Long orgId, LocalDateTime start, LocalDateTime end
  );

  // 진행 중인 투표
  List<Poll> findAllByOrgIdAndStatusOrderByEndsAtAsc(Long orgId, PollStatus status);
}