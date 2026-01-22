package org.example.tackit.domain.Notice_board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticePostRespDto {
    private long id;
    private final String writer;
    private String profileImageUrl;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private String imageUrl;

    private boolean isScrap;
}
