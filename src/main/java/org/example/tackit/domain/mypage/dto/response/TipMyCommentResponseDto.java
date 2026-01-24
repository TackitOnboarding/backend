package org.example.tackit.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Post;
import org.example.tackit.domain.entity.TipComment;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipMyCommentResponseDto {
    private Long commentId;
    private Long postId;
    private String writer;
    private String profileImageUrl;
    private String content;
    private LocalDateTime createdAt;
    private Post type;

    public static TipMyCommentResponseDto fromEntity(TipComment comment) {
        return TipMyCommentResponseDto.builder()
                .commentId(comment.getId())
                .postId(comment.getTipPost().getId())
                .writer(comment.getWriter().getNickname())
                .profileImageUrl(comment.getWriter().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .type(comment.getTipPost().getType())
                .build();
    }

}
