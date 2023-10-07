package com.ltj.user_center.exception;

import com.ltj.user_center.common.BaseResponse;
import com.ltj.user_center.common.ErrorCode;
import com.ltj.user_center.common.ResultUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理自定义异常
    @ExceptionHandler(BusinessException.class)
    public BaseResponse handleMyException(BusinessException e){
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), e.getDetailed());
    }

    //处理系统异常
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleException(RuntimeException e){
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "系统异常报错");
    }
}
