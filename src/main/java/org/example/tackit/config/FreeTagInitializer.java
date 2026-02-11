package org.example.tackit.config;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.freeBoard.Free_tag.repository.FreeTagRepository;
import org.example.tackit.domain.entity.FreeTag;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component
@RequiredArgsConstructor
public class FreeTagInitializer implements CommandLineRunner {
    private final FreeTagRepository tagRepository;

    @Override
    public void run(String... args) {
        if (tagRepository.count() == 0) {
            tagRepository.saveAll(List.of(
                    FreeTag.builder().tagName("활동일상").build(),
                    FreeTag.builder().tagName("맛집추천").build(),
                    FreeTag.builder().tagName("자료공유").build(),
                    FreeTag.builder().tagName("자유토론").build(),
                    FreeTag.builder().tagName("취미생활").build()
            ));
        }
    }

}
