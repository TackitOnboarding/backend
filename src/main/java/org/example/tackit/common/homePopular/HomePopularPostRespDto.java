//package org.example.tackit.common.homePopular;
//
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//import org.example.tackit.domain.entity.*;
//
//import java.time.LocalDateTime;
//
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class HomePopularPostRespDto {
//
//    private long id;
//    private String writer;
//    private String profileImageUrl;
//    private String title;
//    private String content; // 150자 이하
//    private LocalDateTime createdAt;
//    private String type; // 게시판 구분: FREE, QNA, TIP
//    private Long viewCount;
//    private Long scrapCount;
//
//    // FreePost → DTO 변환
//    public static HomePopularPostRespDto fromFree(FreePost post) {
//        return new HomePopularPostRespDto(
//                post.getId(),
//                post.getWriter().getNickname(),
//                post.getWriter().getProfileImageUrl(),
//                post.getTitle(),
//                truncate(post.getContent()),
//                post.getCreatedAt(),
//                "FREE_POST",
//                post.getViewCount(),
//                post.getScrapCount()
//        );
//    }
//
//    // QnAPost → DTO 변환
//    public static HomePopularPostRespDto fromQna(QnAPost post) {
//        return new HomePopularPostRespDto(
//                post.getId(),
//                post.getWriter().getNickname(),
//                post.getWriter().getProfileImageUrl(),
//                post.getTitle(),
//                truncate(post.getContent()),
//                post.getCreatedAt(),
//                "QNA_POST",
//                post.getViewCount(),
//                post.getScrapCount()
//        );
//    }
//
//    // TipPost → DTO 변환
//    public static HomePopularPostRespDto fromTip(TipPost post) {
//        return new HomePopularPostRespDto(
//                post.getId(),
//                post.getWriter().getNickname(),
//                post.getWriter().getProfileImageUrl(),
//                post.getTitle(),
//                truncate(post.getContent()),
//                post.getCreatedAt(),
//                "TIP_POST",
//                post.getViewCount(),
//                post.getScrapCount()
//        );
//    }
//
//    // 본문 150자로 제한
//    private static String truncate(String content) {
//        if (content == null) return "";
//        return content.length() > 150 ? content.substring(0, 150) + "..." : content;
//    }
//}
