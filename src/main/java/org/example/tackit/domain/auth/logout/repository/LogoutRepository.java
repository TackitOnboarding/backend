package org.example.tackit.domain.auth.logout.repository;

import org.example.tackit.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogoutRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
