package com.cz.registry.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * exception
 *
 * @author Zjianru
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegistryException  extends RuntimeException {

    /**
     * 异常码  {@link ExErrorCodes}
     */
    private String errorCode;
    /**
     * 异常描述 {@link ExErrorCodes}
     */
    private String errorDesc;
    /**
     * 异常类型 {@link ExErrorCodes}
     */
    private String errorType;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, ExErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.getErrorCode();
        this.errorDesc = errorCode.getErrorDesc();
        this.errorType = errorCode.getErrorType();
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public RegistryException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @since 1.7
     */
    protected RegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * RPC 通信间异常
     */
    public RegistryException() {
    }

    /**
     * RPC 通信间异常
     *
     * @param errorCode RPC 自定义异常类型
     */
    public RegistryException(ExErrorCodes errorCode) {
        this.errorCode = errorCode.getErrorCode();
        this.errorDesc = errorCode.getErrorDesc();
        this.errorType = errorCode.getErrorType();
    }

    /**
     * RPC 通信间异常
     *
     * @param cause     Ex
     * @param errorCode RPC 自定义异常类型
     */
    public RegistryException(Throwable cause, ExErrorCodes errorCode) {
        super(cause);
        this.errorCode = errorCode.getErrorCode();
        this.errorDesc = errorCode.getErrorDesc();
        this.errorType = errorCode.getErrorType();
    }

}