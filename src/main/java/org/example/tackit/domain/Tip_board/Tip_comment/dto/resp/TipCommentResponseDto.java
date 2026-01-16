package org.example.tackit.domain.Tip_board.Tip_comment.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.TipComment;
import org.example.tackit.domain.entity.Role;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TipCommentResponseDto {
    private final long id;
    private final String writer;
    private final String profileImageUrl;
    private final String content;
    private final LocalDateTime createdAt;
    private final Role role;
    private final int joinedYear;

    public TipCommentResponseDto(TipComment comment) {
        this.id = comment.getId();
        this.writer = comment.getWriter().getNickname();
        this.profileImageUrl = comment.getWriter().getProfileImageUrl();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.role = comment.getWriter().getRole();
        this.joinedYear = comment.getWriter().getJoinedYear();
    }
}
