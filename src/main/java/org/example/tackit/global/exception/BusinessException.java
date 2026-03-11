package org.example.tackit.global.exception;

public class BusinessException extends CustomBaseException {
  public BusinessException(ErrorCode errorCode) {
    super(errorCode);
  }
}
