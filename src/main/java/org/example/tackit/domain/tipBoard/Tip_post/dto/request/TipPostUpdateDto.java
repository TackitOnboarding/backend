package org.example.tackit.domain.tipBoard.Tip_post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TipPostUpdateDto {
    private String title;
    private String content;
    private List<Long> tagIds;
    private Boolean removeImage; // true 면 이미지 삭제
}
