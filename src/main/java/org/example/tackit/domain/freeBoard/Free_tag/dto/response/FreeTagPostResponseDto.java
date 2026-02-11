package org.example.tackit.domain.freeBoard.Free_tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.FreePost;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class FreeTagPostResponseDto {
    private final Long postId;
    private final String writer;
    private final String profileImageUrl;
    private final String title;
    private final String content;
    private final List<String> tags;
    private final LocalDateTime createdAt;
    private final String imageUrl;

    public FreeTagPostResponseDto(Long postId, FreePost post, List<String> tags, String imageUrl) {
        this.postId = postId;
        this.writer = post.getWriter().getNickname();
        this.profileImageUrl = post.getWriter().getProfileImageUrl();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.tags = tags;
        this.createdAt = post.getCreatedAt();
        this.imageUrl = imageUrl;
    }
}
