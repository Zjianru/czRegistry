package com.cz.registry.cluster.connect.impl;

import com.alibaba.fastjson2.JSON;
import com.cz.registry.cluster.connect.Channel;
import com.cz.registry.exception.ExErrorCodes;
import com.cz.registry.exception.RegistryException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

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
     * @param url   url
     * @param param param
     * @return response
     */
    @Override
    public <T> T post(String url, String param, Class<T> clazz) {
        log.debug("czRegistry==> [method]postConnect ==> url=={},param {}", url, param);
        try {
            RequestBody requestBody = RequestBody.create(JSON_TYPE, param);
            log.debug("czRegistry==> [method]postConnect ==> requestBody {}", requestBody);
            Request call = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            String response = client.newCall(call).execute().body().string();
            log.debug("czRegistry==> [method]postConnect ==> response {}", response);
            return JSON.parseObject(response, clazz);
        } catch (Exception e) {
            throw new RegistryException(e, ExErrorCodes.SOCKET_TIME_OUT);
        }
    }

    /**
     * get 方式通信
     *
     * @param url url
     * @return response
     */
    @Override
    public  <T> T  get(String url, Class<T> clazz) {
        log.debug("czRegistry==> [method]getConnect ==> url {}", url);
        try {
            Request call = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            String response = client.newCall(call).execute().body().string();
            log.debug("czRegistry==> [method]getConnect ==> responseMsg {}", response);
            return JSON.parseObject(response, clazz);
        } catch (Exception e) {
            throw new RegistryException(e, ExErrorCodes.SOCKET_TIME_OUT);
        }

    }
}
