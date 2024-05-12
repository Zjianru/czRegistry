package com.cz.registry.meta;

import com.alibaba.fastjson2.JSON;
import lombok.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册实例描述对象
 *
 * @author Zjianru
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"scheme", "host", "port", "context"})
@ToString
public class InstanceMeta {

    /**
     * 服务提供者的地址
     */
    private String host;

    /**
     * 服务提供者的端口
     */
    private Integer port;

    /**
     * 路径
     */
    private String context;

    /**
     * 协议头
     * eg: http / https
     */
    private String scheme;

    /**
     * 蓝绿 - 上线状态
     */
    private boolean status;

    /**
     * 附加参数
     */
    private Map<String, Object> params;


    /**
     * 转换为注册中心的路径
     *
     * @return path
     */
    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    /**
     * 创建一个服务于 http 请求的实例
     *
     * @param host 服务提供者的地址
     * @param port 服务提供者的端口
     * @return InstanceMeta
     */
    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta(host, port, "czRegistry", "http", true, null);
    }

    /**
     * 将信息映射转换为请求使用的 url
     *
     * @return url
     */
    public String transferToUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    /**
     * 将附加参数列表转换为 json 串
     *
     * @return json string
     */
    public String metasTransfer() {
        return JSON.toJSONString(getParams());
    }

    /**
     * 设置附加参数列表
     *
     * @param params 附加参数
     * @return instanceMeta
     */
    public InstanceMeta addParams(Map<String, String> params) {
        Map<String, Object> peek = this.getParams();
        if (peek == null) {
            peek = new HashMap<>();
        }
        peek.putAll(params);
        this.setParams(peek);
        return this;
    }


    /**
     * 从给定的URL创建一个InstanceMeta实例。
     *
     * @param instanceUtl 待解析的实例URL字符串。
     * @return InstanceMeta实例，包含了URL的主机名、端口号、路径、协议等信息。
     */
    public static InstanceMeta fromUrl(String instanceUtl) {
        // 解析输入的URL字符串为URI对象
        URI uri = URI.create(instanceUtl);
        // 基于URI对象的信息，创建并返回一个新的InstanceMeta实例
        return new InstanceMeta(uri.getHost(),
                uri.getPort(),
                uri.getPath().substring(1), // 路径去除第一个字符("/")，因为它是根路径的标识
                uri.getScheme(),
                true, null); // 设置InstanceMeta为活动状态，默认参数为null
    }


}
