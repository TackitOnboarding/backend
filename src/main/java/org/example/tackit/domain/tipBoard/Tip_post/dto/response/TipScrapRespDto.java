package org.example.tackit.domain.tipBoard.Tip_post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TipScrapRespDto {
    private boolean scrap;       // 현재 찜 상태
    private LocalDateTime savedAt;  // 찜한 시각 (찜일 경우만 값 있음)
}
