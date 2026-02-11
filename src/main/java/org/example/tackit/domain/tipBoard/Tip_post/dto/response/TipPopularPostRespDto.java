package org.example.tackit.domain.tipBoard.Tip_post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.TipPost;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TipPopularPostRespDto {
    private long id;
    private String writer;
    private String profileImageUrl;
    private String title;
    private String content; // 150자 이하로 잘린 본문
    private List<String> tags;
    private LocalDateTime createdAt;
    private String imageUrl;

    public static TipPopularPostRespDto from(TipPost post) {
        return new TipPopularPostRespDto(
                post.getId(),
                post.getWriter().getNickname(),
                post.getWriter().getProfileImageUrl(),
                post.getTitle(),
                truncateContent(post.getContent()),
                post.getTagMaps().stream()
                        .map(map -> map.getTag().getTagName()) // TipTagMap → TipTag 구조 가정
                        .toList(),
                post.getCreatedAt(),
                extractFirstImageUrl(post)
        );
    }

    // 본문을 150자 이하로 제한
    private static String truncateContent(String content) {
        if (content == null) return "";
        if (content.length() > 150) {
            return content.substring(0, 150) + "...";
        }
        return content;
    }

    // 이미지
    private static String extractFirstImageUrl(TipPost post) {
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            return post.getImages().get(0).getImageUrl();
        }
        return null;
    }
}
