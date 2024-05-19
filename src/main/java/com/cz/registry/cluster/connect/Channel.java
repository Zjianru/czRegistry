package com.cz.registry.cluster.connect;


/**
 * 通信能力提供
 *
 * @author Zjianru
 */
public interface Channel {

    /**
     * post 方式通信
     *
     * @param url   url
     * @param param param
     * @param clazz clazz
     * @return response
     */
    <T> T post(String url, String param, Class<T> clazz);

    /**
     * get 方式通信
     *
     * @param url url
     * @param clazz clazz
     * @return response
     */
    <T> T get(String url, Class<T> clazz);

}
