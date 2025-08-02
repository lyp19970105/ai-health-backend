package com.example.healthmonitoring.exception;

import com.example.healthmonitoring.dto.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("[全局异常] 捕获到业务异常 -> Code: {}, Message: {}", e.getCode(), e.getMessage(), e);
        return BaseResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("[全局异常] 捕获到未处理的运行时异常 -> {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.SYSTEM_ERROR, "系统内部异常，请联系管理员");
    }
}
