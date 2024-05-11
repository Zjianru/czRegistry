package com.cz.registry.controller;

import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * rest controller for registry
 *
 * @author Zjianru
 */
@RestController
@Slf4j
public class RegistryController {

    @Autowired
    RegistryService registryService;


    /**
     * 注册服务实例。
     *
     * @param service 要注册的服务名称。
     * @param host    服务实例所在的主机地址。
     * @param port    服务实例监听的端口号。
     * @return InstanceMeta 服务注册后的实例元数据。
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public InstanceMeta register(@RequestParam String service, String host, Integer port) {
        // 记录注册服务的日志信息
        log.info("register service:{} host:{} port:{}", service, host, port);
        // 调用注册服务，返回实例的元数据
        return registryService.register(service, InstanceMeta.http(host, port));
    }

    /**
     * 通过POST请求注册服务实例。
     *
     * @param service  要注册的服务名称。
     * @param instance 需要注册的服务实例信息，以JSON格式通过请求体传入。
     * @return InstanceMeta 注册后的服务实例元数据。
     */
    @RequestMapping(value = "/registerByPost", method = RequestMethod.POST)
    public InstanceMeta registerByPost(@RequestParam String service, @RequestBody InstanceMeta instance) {
        // 记录注册服务的日志
        log.info("register service:{} instance:{}", service, instance);
        // 调用注册服务，返回注册结果
        return registryService.register(service, instance);
    }


    /**
     * 取消注册指定的服务实例。
     *
     * @param service 要取消注册的服务名称。
     * @param host    要取消注册的服务实例的主机地址。
     * @param port    要取消注册的服务实例的端口号。
     * @return InstanceMeta 该方法返回取消注册的服务实例的元数据。
     */
    @RequestMapping(value = "/unregister", method = RequestMethod.GET)
    public InstanceMeta unregister(@RequestParam String service, String host, Integer port) {
        // 记录取消注册服务的请求信息
        log.info("unregister service:{} host:{} port:{}", service, host, port);
        // 调用注册中心服务，取消注册指定的服务实例
        return registryService.unregister(service, InstanceMeta.http(host, port));
    }


    /**
     * 请求处理函数，用于获取指定服务的所有实例信息。
     *
     * @param service 需要查询的服务名称。
     * @return 返回一个包含该服务所有实例信息的列表。
     */
    @RequestMapping(value = "/fetchAll", method = RequestMethod.GET)
    public List<InstanceMeta> fetchAll(@RequestParam String service) {
        // 记录请求日志
        log.info("fetchAll service:{}", service);
        // 调用服务注册中心，查询并返回指定服务的所有实例信息
        return registryService.fetchAll(service);
    }

}
