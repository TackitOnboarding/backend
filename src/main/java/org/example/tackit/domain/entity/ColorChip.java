package org.example.tackit.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorChip {
  BLUE("blue"),
  GRAY("gray"),
  PINK("pink"),
  ORANGE("orange"),
  GREEN("green");

  private final String colorName;

  // 프론트엔드 -> 백엔드 역직렬화 용
  @JsonCreator
  public static ColorChip from(String value) {
    for (ColorChip color : ColorChip.values()) {
      if (color.getColorName().equalsIgnoreCase(value)) {
        return color;
      }
    }
    throw new IllegalArgumentException("지원하지 않는 컬러칩 색상입니다: " + value);
  }

  // 백엔드 -> 프론트엔드 직렬화 용
  @JsonValue
  public String getColorName() {
    return colorName;
  }
}