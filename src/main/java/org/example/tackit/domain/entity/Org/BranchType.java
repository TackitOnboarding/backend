package org.example.tackit.domain.entity.Org;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BranchType {
    MAIN("본교"),
    BRANCH("분교");

    private final String description;

    // 한글 명칭으로 Enum을 찾아주는 편의 메서드 (데이터 초기화 시 유용)
    public static BranchType fromDescription(String description) {
        for (BranchType type : BranchType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        return MAIN; // 기본값 설정 혹은 에러 처리
    }
}
