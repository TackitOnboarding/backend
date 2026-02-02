package org.example.tackit.domain.Organization.repository;

import org.example.tackit.domain.entity.Org.Club;
import org.example.tackit.domain.entity.Org.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByNameAndSchool(String name, School school);
}
