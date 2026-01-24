package org.example.tackit.domain.Notice_board.Notice_post.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.NoticePost;

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

    @Builder.Default
    private boolean isScrap = false; // 기본값

    private boolean commentEnabled;

    public static NoticePostRespDto from(NoticePost post, String imageUrl) {
        return NoticePostRespDto.builder()
                .id(post.getId())
                .writer(post.getWriter().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .imageUrl(imageUrl)
                .isScrap(false)
                .commentEnabled(post.isCommentEnabled())
                .build();
    }
}
