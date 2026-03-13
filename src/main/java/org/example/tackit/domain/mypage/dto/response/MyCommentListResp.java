package org.example.tackit.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.post.Comment;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.entity.post.PostType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyCommentListResp {
    private Long commentId;
    private Long postId;
    private PostType postType;
    private String postTitle;
    private String commentContent;
    private LocalDateTime createdAt;

    public static MyCommentListResp from(Comment comment) {
        Post post = comment.getPost();

        return MyCommentListResp.builder()
                .commentId(comment.getId())
                .postId(post.getId())
                .postType(post.getPostType())
                .postTitle(post.getTitle())
                .commentContent(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }


}
