package com.ltj.user_center.common;

/**
 * 错误情况 枚举类
 * 枚举多个错误情况
 */
public enum ErrorCode {
    SYSTEM_ERROR("4005","系统错误","系统错误"),
    PARAMETER_ERROR("4001","请求参数错误","请求参数错误"),
    NULL_ERROR("4002","数据为空","数据为空"),
    NOT_LOGIN("4003","未登录","未登录"),
    NOT_AUTH("4004","无权限","无权限"),
    ACCOUNT_REPEAT("4006","账号重复","账号重复"),
    USER_UNREGISTERED("4007","账号未注册或输入错误","账号未注册或输出错误"),
    ;

    private final String code;
    private final String message;
    private final String detailed;

    ErrorCode(String code, String message, String detailed) {
        this.code = code;
        this.message = message;
        this.detailed = detailed;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailed() {
        return detailed;
    }
}
