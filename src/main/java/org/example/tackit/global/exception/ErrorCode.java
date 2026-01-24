package org.example.tackit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 404
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 사용자입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "게시글이 존재하지 않습니다."),

    // 403
    ACCESS_DENIED_EDIT(HttpStatus.FORBIDDEN, "A001", "작성자만 수정할 수 있습니다."),
    ACCESS_DENIED_ORGANIZATION(HttpStatus.FORBIDDEN, "A002", "해당 조직에 속하지 않은 게시글입니다."),
    ACCESS_DENIED_DELETE(HttpStatus.FORBIDDEN, "A003", "작성자 또는 관리자만 삭제할 수 있습니다."),
    ACCESS_DENIED_NOTICE(HttpStatus.FORBIDDEN, "A004", "운영진만 공지 게시글을 작성할 수 있습니다."),

    // 400
    POST_IS_INACTIVE(HttpStatus.BAD_REQUEST, "P002", "비활성화된 게시글입니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "요청을 처리하는 중에 서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
