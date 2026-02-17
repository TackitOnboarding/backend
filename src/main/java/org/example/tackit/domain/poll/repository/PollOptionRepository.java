package org.example.tackit.domain.poll.repository;

import java.util.List;
import org.example.tackit.domain.entity.poll.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

  List<PollOption> findAllByPollIdOrderBySeqAsc(Long pollId);
}