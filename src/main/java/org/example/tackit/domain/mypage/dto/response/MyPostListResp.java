package org.example.tackit.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.MemberRole;
import org.example.tackit.domain.entity.MemberType;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.entity.post.PostCategory;
import org.example.tackit.domain.entity.post.PostType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyPostListResp {
    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private String imageUrl;
    private String postUrl;
    private PostType postType;
    private PostCategory category;
    private MemberRole memberRole;
    private MemberType memberType;
    private LocalDateTime createdAt;

    public static MyPostListResp from(Post post) {
        MemberOrg writer = post.getWriter();

        String imageUrl = post.getImages().isEmpty() ? null
                : post.getImages().get(0).getImageUrl();

        String url = "/api/posts/" + post.getId();

        String displayNickname = post.isAnonymous() ? "익명" : writer.getNickname();

        return MyPostListResp.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(displayNickname)
                .imageUrl(imageUrl)
                .postUrl(url)
                .postType(post.getPostType())
                .category(post.getCategory())
                .memberRole(writer.getMemberRole())
                .memberType(writer.getMemberType())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
