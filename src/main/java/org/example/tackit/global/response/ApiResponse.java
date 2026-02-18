package org.example.tackit.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

  private Status status;
  private T content;

  /**
   * 성공 (데이터 있음) 예: ApiResponse.success(HttpStatus.OK, "조회 성공", data)
   */
  public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus httpStatus, String message,
      T content) {
    return ResponseEntity
        .status(httpStatus)
        .body(new ApiResponse<>(new Status(httpStatus.value(), message), content));
  }

  /**
   * 성공 (데이터 없음) 예: ApiResponse.success(HttpStatus.OK, "삭제 성공")
   */
  public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus httpStatus, String message) {
    return ResponseEntity
        .status(httpStatus)
        .body(new ApiResponse<>(new Status(httpStatus.value(), message), null));
  }

  /**
   * 실패 (에러 메시지만 반환) 예: ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 요청입니다")
   * TODO 추후 exception 쪽에서 사용하도록 수정해야함
   */
  public static <T> ResponseEntity<ApiResponse<T>> fail(HttpStatus httpStatus, String message) {
    return ResponseEntity
        .status(httpStatus)
        .body(new ApiResponse<>(new Status(httpStatus.value(), message), null));
  }

  @Getter
  @AllArgsConstructor
  public static class Status {

    private int code;
    private String message;
  }
}