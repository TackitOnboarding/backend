package org.example.tackit.domain.university.repository;

import org.example.tackit.domain.entity.Org.University;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    // 학교명으로 데이터 존재 여부 확인 (중복 체크용)
    Optional<University> findByUniversityName(String universityName);

    // 학교명 부분 일치 검색
    // ex. "서울" 입력 시 "서울대학교", "서울시립대학교"
    List<University> findByUniversityNameContaining(String name, Pageable pageable);

    // 초성 부분 일치 검색
    // ex. "ㅅㅁㅇㄷ" 입력 시 "숙명여자대학교"
    List<University> findByUniversityChosungContaining(String chosung, Pageable pageable);
}
