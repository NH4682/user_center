package com.ltj.user_center.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回类 Response
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private String code;
    private T data;
    private String message;
    private String detailed;

    public BaseResponse(String code, T data, String message, String detailed) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.detailed = detailed;
    }
    public BaseResponse(String code, T data, String message) {
        this(code,data,message,"");
    }
    public BaseResponse(String code, T data) {
        this(code,data,"","");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDetailed());
    }
}
