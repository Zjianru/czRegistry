package com.cz.registry.cluster.connect.impl;

import com.alibaba.fastjson2.JSON;
import com.cz.registry.cluster.connect.Channel;
import com.cz.registry.common.RegistryRequest;
import com.cz.registry.exception.ExErrorCodes;
import com.cz.registry.exception.RegistryException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * code desc
 *
 * @author Zjianru
 */
@Slf4j
public class HttpCall implements Channel {

    private final OkHttpClient client;

    MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    public HttpCall(int timeout) {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(timeout, TimeUnit.MICROSECONDS)
                .writeTimeout(timeout, TimeUnit.MICROSECONDS)
                .connectTimeout(timeout, TimeUnit.MICROSECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * post 方式通信
     *
     * @param request 封装后的请求信息
     * @return response
     */
    @Override
    public String post(RegistryRequest request) {
        log.debug("czRegistry==> [method]postConnect ==> param {}", request);
        try {
            Request call = new Request.Builder()
                    .url(request.getUrl())
                    .post(RequestBody.create(JSON_TYPE, JSON.toJSONString(request.getParams())))
                    .build();
            String response = Objects.requireNonNull(client.newCall(call).execute().body()).string();
            log.debug("czRegistry==> [method]postConnect ==> response {}", response);
            return response;
        } catch (Exception e) {
            throw new RegistryException(e, ExErrorCodes.SOCKET_TIME_OUT);
        }
    }

    /**
     * get 方式通信
     *
     * @param request 封装后的请求信息
     * @return response
     */
    @Override
    public String get(RegistryRequest request) {
        log.debug("czRegistry==> [method]getConnect ==> param {}", request.getUrl());
        try {
            Request call = new Request.Builder()
                    .url(request.getUrl())
                    .get()
                    .build();
            String response = Objects.requireNonNull(client.newCall(call).execute().body()).string();
            log.debug("czRegistry==> [method]getConnect ==> response {}", response);
            return response;
        } catch (Exception e) {
            throw new RegistryException(e, ExErrorCodes.SOCKET_TIME_OUT);
        }

    }
}
