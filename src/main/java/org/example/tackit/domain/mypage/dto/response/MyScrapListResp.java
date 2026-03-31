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
import org.example.tackit.domain.entity.post.Scrap;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyScrapListResp {
    private Long scrapId;
    private Long postId;
    private String title;
    private String content;
    private String writerNickname;
    private MemberType memberType;
    private MemberRole memberRole;
    private PostType postType;
    private PostCategory postCategory;
    private String imageUrl;
    private String postUrl;

    private LocalDateTime createdAt;

    public static MyScrapListResp from(Scrap scrap) {
        Post post = scrap.getPost();
        MemberOrg writer = post.getWriter();

        String imageUrl = !post.getImages().isEmpty()
                ? post.getImages().get(0).getImageUrl()
                : null;

        String url = "/api/posts/" + post.getId();

        String displayNickname = post.isAnonymous() ? "익명" : writer.getNickname();

        return MyScrapListResp.builder()
                .scrapId(scrap.getId())
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerNickname(displayNickname)
                .memberRole(post.getWriter().getMemberRole())
                .memberType(post.getWriter().getMemberType())
                .postType(post.getPostType())
                .postCategory(post.getCategory())
                .imageUrl(imageUrl)
                .postUrl(url)
                .createdAt(scrap.getCreatedAt())
                .build();
    }
}
