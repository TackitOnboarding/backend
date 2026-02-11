package org.example.tackit.domain.tipBoard.Tip_tag.dto.response;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TipTagPostResponseDto {
    private final Long postId;
    private final String writer;
    private final String profileImageUrl;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private List<String> tags;
    private final String imageUrl;

    public TipTagPostResponseDto(Long postId, String writer, String profileImageUrl, String title, String content, LocalDateTime createdAt, List<String> tags, String imageUrl) {
        this.postId = postId;
        this.writer = writer;
        this.profileImageUrl = profileImageUrl;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.tags = tags;
        this.imageUrl = imageUrl;
    }
}
