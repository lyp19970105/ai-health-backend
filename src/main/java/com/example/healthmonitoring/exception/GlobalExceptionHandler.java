package com.example.healthmonitoring.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * 全局异常处理器
 * 处理安全相关的异常并返回标准化的错误响应
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理访问被拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        logger.error("访问被拒绝: {}", ex.getMessage());

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "访问被拒绝",
                "您没有权限访问此资源",
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        logger.error("认证失败: {}", ex.getMessage());

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "认证失败",
                ex.getMessage(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理错误凭证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {

        logger.error("凭证无效: {}", ex.getMessage());

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "凭证无效",
                "用户名或密码不正确",
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理JWT相关异常
     */
    @ExceptionHandler({
        ExpiredJwtException.class,
        UnsupportedJwtException.class,
        MalformedJwtException.class,
        SignatureException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleJwtException(
            Exception ex, WebRequest request) {

        logger.error("JWT令牌无效: {}", ex.getMessage());

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String errorMessage;

        if (ex instanceof ExpiredJwtException) {
            errorMessage = "JWT令牌已过期";
        } else if (ex instanceof UnsupportedJwtException) {
            errorMessage = "不支持的JWT令牌";
        } else if (ex instanceof MalformedJwtException) {
            errorMessage = "JWT令牌格式不正确";
        } else if (ex instanceof SignatureException) {
            errorMessage = "JWT签名验证失败";
        } else {
            errorMessage = "JWT令牌无效";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT认证失败",
                errorMessage,
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        logger.error("发生未处理的异常: ", ex);

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "服务器错误",
                "处理请求时发生内部错误",
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
