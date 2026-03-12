package org.example.tackit.domain.organization.repository;

import org.example.tackit.domain.entity.org.OrgType;
import org.example.tackit.domain.entity.org.Organization;
import org.example.tackit.domain.entity.org.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    boolean existsByNameAndUniversityAndType(String name, University university, OrgType type);
    boolean existsByNameAndType(String name, OrgType type);
    boolean existsById(Long id);
    List<Organization> findByTypeAndNameContaining(OrgType type, String name);
}
