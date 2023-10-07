package com.ltj.user_center.common;

/**
 * 返回类的工具类
 */
public class ResultUtils {
    //成功类
    public static <T> BaseResponse<T> success( T data){
        return new BaseResponse<>("0",data,"成功");
    }

    //失败类
    public static <T> BaseResponse<T> error( ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }
    public static <T> BaseResponse<T> error(String code,String message,ErrorCode errorCode){
        return new BaseResponse<>(code,null,message,errorCode.getDetailed());
    }
    public static <T> BaseResponse<T> error( String code,String message, String detailed){
        return new BaseResponse<>(code,null,message,detailed);
    }
    public static <T> BaseResponse<T> error( String code,String message){
        return new BaseResponse<>(code,null,message);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message,String detailed){
        return new BaseResponse<>(errorCode.getCode(),null,message,detailed);
    }




}
