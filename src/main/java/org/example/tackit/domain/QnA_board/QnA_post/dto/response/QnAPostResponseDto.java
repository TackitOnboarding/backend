package org.example.tackit.domain.QnA_board.QnA_post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.QnAPost;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class QnAPostResponseDto {
    private final Long postId;
    private final String writer;
    private final String profileImageUrl;
    private final String title;
    private final String content;
    private final List<String> tags;
    private final LocalDateTime createdAt;
    private String imageUrl;

    private boolean isScrap;

    private boolean isAnonymous;

    public static QnAPostResponseDto fromEntity(QnAPost post, List<String> tagNames, boolean isScrap) {
        String imageUrl = post.getImages().isEmpty() ? null
                : post.getImages().get(0).getImageUrl();

        // 익명 여부 확인
        boolean anonymous = post.isAnonymous();

        return QnAPostResponseDto.builder()
                .postId(post.getId())
                .writer(post.isAnonymous() ? "익명" : post.getWriter().getNickname())
                .profileImageUrl(post.isAnonymous() ? null : post.getWriter().getProfileImageUrl())
                // .writer(post.getWriter().getNickname())
                // .profileImageUrl(post.getWriter().getProfileImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(tagNames)
                .createdAt(post.getCreatedAt())
                .imageUrl(imageUrl)
                .isScrap(isScrap)
                .isAnonymous(anonymous)
                .build();
    }
}
