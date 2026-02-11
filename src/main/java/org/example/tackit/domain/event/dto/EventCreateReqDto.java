package org.example.tackit.domain.event.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.tackit.domain.entity.EventScope;
import org.example.tackit.domain.entity.Org.OrgType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventCreateReqDto {

    @NotNull(message = "조직 ID는 필수입니다.")
    private Long orgId;

    @NotBlank(message = "일정 제목은 필수입니다.")
    private String title;

    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalDateTime startsAt;

    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalDateTime endsAt;

    private String description;

    @NotNull(message = "참여자 범위(eventScope)는 필수입니다.")
    private EventScope eventScope;

    @NotNull(message = "참여자 목록 필드는 필수입니다.")
    private List<Long> participants; 

    @NotBlank(message = "색상 코드는 필수입니다.")
    private String colorChip;
}