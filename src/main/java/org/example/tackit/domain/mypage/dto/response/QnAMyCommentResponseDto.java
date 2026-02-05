package org.example.tackit.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.entity.Post;
import org.example.tackit.domain.entity.QnAComment;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnAMyCommentResponseDto {
    private Long commentId;
    private Long postId;
    private String writer;
    private String profileImageUrl;
    private String content;
    private LocalDateTime createdAt;
    private Post type;

    public static QnAMyCommentResponseDto fromEntity(QnAComment comment) {
        return QnAMyCommentResponseDto.builder()
                .commentId(comment.getId())
                .postId(comment.getQnAPost().getId())
                .writer(comment.getWriter().getNickname())
                .profileImageUrl(comment.getWriter().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .type(comment.getQnAPost().getType())
                .build();
    }

}

