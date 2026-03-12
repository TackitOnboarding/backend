package org.example.tackit.domain.university.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.org.University;
import org.example.tackit.domain.university.dto.UniversityRespDto;
import org.example.tackit.domain.university.repository.UniversityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniversityService {

  private final UniversityRepository universityRepository;

  public List<UniversityRespDto> searchUniversities(String word) {
    String trimWord = word.trim();
    boolean isChosung = trimWord.matches("^[ㄱ-ㅎ]+$");

    Pageable limitTen = PageRequest.of(0, 10, Sort.by("universityName").ascending());

    List<University> result = isChosung
        ? universityRepository.findByUniversityChosungContaining(trimWord, limitTen)
        : universityRepository.findByUniversityNameContaining(trimWord, limitTen);

    return result.stream()
        .map(UniversityRespDto::from)
        .collect(Collectors.toList());
  }
}
