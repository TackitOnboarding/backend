package org.example.tackit.domain.qnaBoard.QnA_comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QnACommentCreateDto {
    private long qnaPostId;
    private String content;
}
