package org.example.tackit.domain.executive.repository;

import org.example.tackit.domain.entity.org.MemberOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutiveMemberRepository extends JpaRepository<MemberOrg, Long> {

    Page<MemberOrg> findByOrganizationId(Long organizationId, Pageable pageable);
}
