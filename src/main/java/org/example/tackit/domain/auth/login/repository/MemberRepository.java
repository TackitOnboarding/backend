package org.example.tackit.domain.auth.login.repository;

import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); //그 유저 실제 정보 추출
    boolean existsByEmail(String email); // 이메일 존재 확인
    boolean existsByNickname(String nickname); //닉네임 중복 확인
    boolean existsByEmailAndStatus(String email, Status status); // 이메일+상태 존재 확인
    Optional<Member> findByEmailAndStatus(String email, Status status); // 이메일+상태 정보 추출

    Optional<Member> findByOrganizationAndName(String organization, String name);

    Optional<Member> findByEmailAndOrganization(String email, String organization);

    Optional<Member> findByNameAndOrganizationAndEmail(String name, String organization, String email);
}

