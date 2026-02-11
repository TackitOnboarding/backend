package org.example.tackit.domain.qnaBoard.QnA_tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnATagResponseDto {
    private Long id;
    private String tagName;
}
