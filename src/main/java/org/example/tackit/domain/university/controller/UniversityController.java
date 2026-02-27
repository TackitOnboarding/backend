package org.example.tackit.domain.university.controller;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.university.dto.UniversityRespDto;
import org.example.tackit.domain.university.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@RequiredArgsConstructor
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("/search")
    public ResponseEntity<List<UniversityRespDto>> search(
            @RequestParam String word
    ) {
        return ResponseEntity.ok(universityService.searchUniversities(word));
    }
}
