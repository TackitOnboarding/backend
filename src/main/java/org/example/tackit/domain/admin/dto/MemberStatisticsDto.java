package org.example.tackit.domain.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberStatisticsDto {
    private final long totalCount;
    private final long monthlyCount;
    private final long weeklyCount;

    public MemberStatisticsDto toDTO() {
        return new MemberStatisticsDto(totalCount, monthlyCount, weeklyCount);
    }
}
