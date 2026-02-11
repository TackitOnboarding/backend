package org.example.tackit.domain.organization.repository;

import org.example.tackit.domain.entity.Org.OrgType;
import org.example.tackit.domain.entity.Org.Organization;
import org.example.tackit.domain.entity.Org.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    // 특정 학교 내에 같은 이름의 CLUB 있는지
    Optional<Organization> findByNameAndSchoolAndType(String name, School school, OrgType type);

    // 같은 이름의 COMMUNITY 있는지
    Optional<Organization> findByNameAndType(String name, OrgType type);
}
