package org.example.tackit.domain.qnaBoard.QnA_post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.QnAPost;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QnAPopularPostRespDto {
    private long id;
    private String writer;
    private String profileImageUrl;
    private String title;
    private String content; // 150자 이하
    private List<String> tags;
    private LocalDateTime createdAt;
    private String imageUrl;

    public static QnAPopularPostRespDto from(QnAPost post) {
        return new QnAPopularPostRespDto(
                post.getId(),
                post.getWriter().getNickname(),
                post.getWriter().getProfileImageUrl(),
                post.getTitle(),
                truncateContent(post.getContent()),
                post.getTagMaps().stream()
                        .map(map -> map.getTag().getTagName())
                        .toList(),
                post.getCreatedAt(),
                extractFirstImageUrl(post)
        );
    }

    // 본문 150자 제한
    private static String truncateContent(String content) {
        if (content == null) return "";
        if (content.length() > 150) {
            return content.substring(0, 150) + "...";
        }
        return content;
    }

    // 이미지
    private static String extractFirstImageUrl(QnAPost post) {
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            return post.getImages().get(0).getImageUrl();
        }
        return null;
    }
}
