package com.ltj.user_center.exception;

import com.ltj.user_center.common.ErrorCode;

public class BusinessException extends RuntimeException{

    private String code;
    private String detailed;


    public BusinessException(String message, String code, String detailed) {
        super(message);
        this.code = code;
        this.detailed = detailed;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.detailed = errorCode.getDetailed();
    }

    public BusinessException(ErrorCode errorCode,String detailed) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.detailed = detailed;
    }

    public String getCode() {
        return code;
    }

    public String getDetailed() {
        return detailed;
    }
}
