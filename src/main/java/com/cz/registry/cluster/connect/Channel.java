package com.cz.registry.cluster.connect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.cz.registry.cluster.connect.impl.HttpCall;
import com.cz.registry.common.RegistryRequest;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 通信能力提供
 *
 * @author Zjianru
 */
public interface Channel {

    Logger log = LoggerFactory.getLogger(Channel.class);

    HttpCall Default = new HttpCall(500);

    /**
     * post 方式通信
     *
     * @param request 封装后的请求信息
     * @return response
     */
    String post(RegistryRequest request);

    /**
     * get 方式通信
     *
     * @param request 封装后的请求信息
     * @return response
     */
    String get(RegistryRequest request);

    @SneakyThrows
    static <T> T httpGet(String url, Class<T> clazz) {
        log.debug(" czRegistry =====>>>>>> method is httpGet ==> url{}, target class is {}", url, clazz);
        RegistryRequest request = RegistryRequest.builder()
                .url(url)
                .build();
        String respJson = Default.get(request);
        log.debug(" czRegistry =====>>>>>> method is httpGet ==> response{}} ", respJson);
        return JSON.parseObject(respJson, clazz);
    }

    @SneakyThrows
    static <T> T httpGet(String url, TypeReference<T> typeReference) {
        log.debug(" czRegistry =====>>>>>> method is httpGet ==> url{}, TypeReference is {}", url, typeReference);
        RegistryRequest request = RegistryRequest.builder()
                .url(url)
                .build();
        String respJson = Default.get(request);
        log.debug(" czRegistry ======>>>>>> method is httpGet ==> response{}} ", respJson);
        return JSON.parseObject(respJson, typeReference);
    }

    @SneakyThrows
    static <T> T httpPost(Map<String, String> param, String url, Class<T> clazz) {
        log.debug(" czRegistry =====>>>>>> method is httpPost ==> url{}, target class is {}", url, clazz);
        RegistryRequest request = RegistryRequest.builder()
                .url(url)
                .params(param)
                .build();
        String respJson = Default.post(request);
        log.debug(" czRegistry =====>>>>>> method is httpPost ==> response{}} ", respJson);
        return JSON.parseObject(respJson, clazz);
    }
}
