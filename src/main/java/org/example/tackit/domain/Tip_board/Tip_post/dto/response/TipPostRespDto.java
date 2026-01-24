package org.example.tackit.domain.Tip_board.Tip_post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.TipPost;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class TipPostRespDto {
    private long id;
    private final String writer;
    private final String profileImageUrl;
    private final String title;
    private final String content;
    private final List<String> tags;
    private final LocalDateTime createdAt;
    private final String imageUrl;
    private final boolean isScrap;
    private final boolean isAnonymous;

    public TipPostRespDto(TipPost post, List<String> tags) {
        this.id = post.getId();
        this.isAnonymous = post.isAnonymous();
        this.writer = post.isAnonymous() ? "익명" : post.getWriter().getNickname();
        this.profileImageUrl = post.isAnonymous() ? null : post.getWriter().getProfileImageUrl();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.tags = tags;
        this.createdAt = post.getCreatedAt();
        this.imageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();
        this.isScrap = false;

    }
}
