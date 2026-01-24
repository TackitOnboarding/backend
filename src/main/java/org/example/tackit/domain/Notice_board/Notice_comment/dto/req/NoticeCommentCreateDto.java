package org.example.tackit.domain.Notice_board.Notice_comment.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCommentCreateDto {
    private Long noticePostId;
    private String content;
}
