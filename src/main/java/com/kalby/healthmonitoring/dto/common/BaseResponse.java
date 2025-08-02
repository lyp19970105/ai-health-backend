package com.kalby.healthmonitoring.dto.common;

import com.kalby.healthmonitoring.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用API响应封装类。
 * <p>
 * 该类为所有HTTP API提供了一个标准化的响应结构。通过将返回数据包装在该对象中，
 * 可以确保前端接收到的JSON格式始终保持一致，包含了业务状态码、响应数据和描述信息。
 *
 * @param <T> 响应数据（data）的泛型类型。
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码。
     * <p>
     * 0 表示成功，其他非零值表示具体的错误类型。
     * 状态码的定义遵循 {@link ErrorCode} 枚举。
     */
    private int code;

    /**
     * 响应数据。
     * <p>
     * 当请求成功时，此字段包含返回的业务数据。
     * 当请求失败时，此字段通常为 null。
     */
    private T data;

    /**
     * 响应消息。
     * <p>
     * 用于提供关于请求结果的文本描述。
     * 成功时通常为 "ok"，失败时则为具体的错误信息。
     */
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 创建一个表示成功的响应对象。
     *
     * @param data 要返回的业务数据。
     * @param <T>  响应数据的类型。
     * @return 包含成功状态码、数据和默认成功消息的 {@link BaseResponse} 实例。
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data, ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 创建一个表示失败的响应对象。
     *
     * @param errorCode 描述错误类型的枚举实例。
     * @return 包含错误码和错误信息的 {@link BaseResponse} 实例。
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 创建一个表示失败的响应对象，使用自定义的错误消息。
     *
     * @param errorCode 描述错误类型的枚举实例。
     * @param message   自定义的、覆盖默认错误信息的描述。
     * @return 包含错误码和自定义错误信息的 {@link BaseResponse} 实例。
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

    /**
     * 创建一个表示失败的响应对象，使用自定义的错误码和消息。
     *
     * @param code    自定义的业务错误码。
     * @param message 自定义的错误信息。
     * @return 包含指定错误码和错误信息的 {@link BaseResponse} 实例。
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
}