package org.example.tackit.domain.poll.repository;

import java.util.List;
import org.example.tackit.domain.entity.poll.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  // 옵션별 득표수 통계
  @Query("SELECT v.pollOption.id, COUNT(v) FROM Vote v WHERE v.participant.poll.id = :pollId GROUP BY v.pollOption.id")
  List<Object[]> countByPollIdGroupByOptionId(@Param("pollId") Long pollId);

  // 유저가 선택한 옵션 ID 조회
  @Query("SELECT v.pollOption.id FROM Vote v WHERE v.participant.id = :participantId")
  List<Long> findOptionIdsByParticipantId(@Param("participantId") Long participantId);

  // 기존 투표 내역 일괄 삭제
  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Vote v WHERE v.participant.id = :participantId")
  void deleteByParticipantId(@Param("participantId") Long participantId);
}