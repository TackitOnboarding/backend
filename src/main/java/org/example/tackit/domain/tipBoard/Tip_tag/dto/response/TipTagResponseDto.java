package org.example.tackit.domain.tipBoard.Tip_tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipTagResponseDto {
    private Long id;
    private String tagName;
}
