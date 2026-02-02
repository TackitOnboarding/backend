package org.example.tackit.domain.Organization.repository;

import org.example.tackit.domain.entity.Org.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
