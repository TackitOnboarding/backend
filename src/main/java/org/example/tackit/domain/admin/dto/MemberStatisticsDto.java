package org.example.tackit.domain.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberStatisticsDTO {
    private final long totalCount;
    private final long monthlyCount;
    private final long weeklyCount;

    public MemberStatisticsDTO toDTO() {
        return new MemberStatisticsDTO(totalCount, monthlyCount, weeklyCount);
    }
}
