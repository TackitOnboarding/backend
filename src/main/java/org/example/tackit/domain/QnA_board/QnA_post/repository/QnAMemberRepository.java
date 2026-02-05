package org.example.tackit.domain.QnA_board.QnA_post.repository;

import org.example.tackit.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QnAMemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}

