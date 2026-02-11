package org.example.tackit.domain.freeBoard.Free_tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeTagResponseDto {
    private Long id;
    private String tagName;
}
