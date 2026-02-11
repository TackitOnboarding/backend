package org.example.tackit.domain.tipBoard.Tip_post.repository;

import org.example.tackit.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipMemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
