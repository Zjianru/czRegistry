package com.cz.registry.exception;

import com.cz.registry.common.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * process controller exception
 *
 * @author Zjianru
 */
@RestControllerAdvice
public class RegistryExceptionHandle {

    /**
     * 处理注册中心相关的异常。
     * 当发生{@link RegistryException}时，将服务器错误（500）作为响应状态码返回给客户端。
     *
     * @param e 异常对象，表示发生的注册中心异常。
     * @return 返回一个包含错误信息和异常对象的响应体{@link Response<String>}。
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RegistryException.class)
    public Response<String> handleControllerException(RegistryException e) {
        // 创建并返回一个包含错误信息和异常实例的响应对象
        return new Response<>(false, e.getMessage(), e);
    }

}
