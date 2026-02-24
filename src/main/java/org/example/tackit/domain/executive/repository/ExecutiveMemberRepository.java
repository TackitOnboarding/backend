package org.example.tackit.domain.executive.repository;


import org.example.tackit.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutiveMemberRepository extends JpaRepository<Member, Long> {
}
