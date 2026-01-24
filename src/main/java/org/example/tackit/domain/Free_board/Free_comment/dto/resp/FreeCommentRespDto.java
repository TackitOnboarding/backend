package org.example.tackit.domain.Free_board.Free_comment.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.FreeComment;
import org.example.tackit.domain.entity.MemberType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FreeCommentRespDto {
    private final long id;
    private final String writer;
    private final String profileImageUrl;
    private final String content;
    private final LocalDateTime createdAt;
    private final MemberType memberType;
    private final int joinedYear;

    public FreeCommentRespDto(FreeComment comment) {
        this.id = comment.getId();
        this.writer = comment.getWriter().getNickname();
        this.profileImageUrl = comment.getWriter().getProfileImageUrl();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.memberType = comment.getWriter().getMemberType();
        this.joinedYear = comment.getWriter().getJoinedYear();
    }
}
