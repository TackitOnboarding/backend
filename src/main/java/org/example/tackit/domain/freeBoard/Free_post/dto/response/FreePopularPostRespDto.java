package org.example.tackit.domain.freeBoard.Free_post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.FreePost;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FreePopularPostRespDto {
    private long id;
    private String writer;
    private String profileImageUrl;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime createdAt;
    private String imageUrl;

    public static FreePopularPostRespDto from(FreePost post) {
        return new FreePopularPostRespDto(
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

    private static String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() > 150) {
            return content.substring(0, 150) + "...";
        }
        return content;
    }

    private static String extractFirstImageUrl(FreePost post) {
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            return post.getImages().get(0).getImageUrl();
        }
        return null;
    }
}

