package org.example.tackit.domain.organization.repository;

import org.example.tackit.domain.entity.Org.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolName(String schoolName);
}
