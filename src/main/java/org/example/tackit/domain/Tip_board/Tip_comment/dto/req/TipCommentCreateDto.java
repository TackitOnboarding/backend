package org.example.tackit.domain.Tip_board.Tip_comment.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TipCommentCreateDto {
    private long tipPostId;
    private String content;
}
