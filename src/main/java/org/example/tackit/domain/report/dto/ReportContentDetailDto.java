package org.example.tackit.domain.report.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.TargetType;

import java.util.List;

@Getter
@Builder
public class ReportContentDetailDto {
    private Long targetId;
    private TargetType targetType;
    private String postType;
    private String contentTitle;
    private String contentWriter;
    private ActiveStatus activeStatus;

    private List<ReportLogDto> reportLogs;

}
