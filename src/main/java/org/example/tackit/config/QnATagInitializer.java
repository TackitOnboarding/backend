package org.example.tackit.config;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.QnA_board.QnA_tag.repository.QnATagRepository;
import org.example.tackit.domain.entity.QnATag;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component
@RequiredArgsConstructor
public class QnATagInitializer implements CommandLineRunner {
    private final QnATagRepository tagRepository;

    @Override
    public void run(String... args) {
        if (tagRepository.count() == 0) {
            tagRepository.saveAll(List.of(
                    QnATag.builder().tagName("활동질문").build(),
                    QnATag.builder().tagName("신입고민").build(),
                    QnATag.builder().tagName("운영&제도").build(),
                    QnATag.builder().tagName("문화적응").build(),
                    QnATag.builder().tagName("소통고민").build()
            ));
        }
    }

}
