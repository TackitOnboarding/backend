package org.example.tackit.config;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.tipBoard.Tip_tag.repository.TipTagRepository;
import org.example.tackit.domain.entity.TipTag;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component
@RequiredArgsConstructor
public class TipTagInitializer implements CommandLineRunner {
    private final TipTagRepository tagRepository;

    @Override
    public void run(String... args) {
        if (tagRepository.count() == 0) {
            tagRepository.saveAll(List.of(
                    TipTag.builder().tagName("유용한_팁").build(),
                    TipTag.builder().tagName("온보딩").build(),
                    TipTag.builder().tagName("팀문화").build(),
                    TipTag.builder().tagName("경험담_공유").build(),
                    TipTag.builder().tagName("교육&멘토링").build()
            ));
        }
    }

}
