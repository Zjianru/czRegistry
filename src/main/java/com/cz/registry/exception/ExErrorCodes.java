package com.cz.registry.exception;


import lombok.Getter;

/**
 * 异常码
 *
 * @author Zjianru
 */
@Getter
public enum ExErrorCodes {
    SOCKET_TIME_OUT("X", "001", "http invoke time out"),
    NO_SUCH_METHOD("X", "002", "method not exits"),
    PROVIDER_ERROR("X", "003", "provider error"),
    PROVIDER_NOT_FOUND("X", "004", "provider not found"),
    REGISTER_CENTER_ERROR("X", "005", "register center error"),
    INVOKER_ERROR("X", "005", "invoker error"),
    TPS_EXCEED_LIMIT("X", "006", "tps exceed limit"),

    UNKNOWN_ERROR("Z", "001", "unknown error"),

    ;
    /**
     * 异常描述
     */
    private final String errorDesc;

    /**
     * 异常类型
     * X --> 技术类异常
     * Y --> 业务类异常
     * Z --> 通用 非常规异常，暂标记为 unknown
     */
    private final String errorType;

    /**
     * 异常码
     */
    private final String errorCode;

    /**
     * constructor
     *
     * @param errorType 异常类型
     * @param errorCode 异常码
     * @param errorDesc 异常描述
     */
    ExErrorCodes(String errorType, String errorCode, String errorDesc) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }
}
