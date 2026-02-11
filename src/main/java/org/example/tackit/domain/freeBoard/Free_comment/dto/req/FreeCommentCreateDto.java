package org.example.tackit.domain.freeBoard.Free_comment.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FreeCommentCreateDto {
    private Long freePostId;
    private String content;
}
