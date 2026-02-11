package org.example.tackit.domain.qnaBoard.QnA_post.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.Post;
import org.example.tackit.domain.entity.QnAPost;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class QnACheckScrapResponseDto {
    private final Long postId;
    private final String title;
    private final String writer;
    private final String writerProfileImageUrl;
    private final String contentPreview;
    private final List<String> tags;
    private final LocalDateTime createdAt;
    private final Post type;
    private final String imageUrl;

    public static QnACheckScrapResponseDto fromEntity(QnAPost post, Post type, List<String> tagNames) {
        String content = post.getContent();
        if (content.length() > 100) {
            content = content.substring(0, 100) + "...";
        }

        return QnACheckScrapResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .writer(post.getWriter().getNickname())
                .writerProfileImageUrl(post.getWriter().getProfileImageUrl())
                .contentPreview(content)
                .tags(tagNames)
                .createdAt(post.getCreatedAt())
                .type(type)
                .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl())
                .build();
    }

}
