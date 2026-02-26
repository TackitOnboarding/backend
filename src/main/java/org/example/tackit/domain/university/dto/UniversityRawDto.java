package org.example.tackit.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UniversityRawDto {
    @JsonProperty("학교명")
    private String schoolName;

    @JsonProperty("본분교구분명")
    private String branchType;

    @JsonProperty("대학구분명")
    private String universityType;

    @JsonProperty("시도코드")
    private String sidoCode;

    @JsonProperty("소재지도로명주소")
    private String address;
}
