package com.cz.registry.service;

import com.cz.registry.meta.InstanceMeta;

import java.util.List;
import java.util.Map;

/**
 * interface of registry service
 *
 * @author Zjianru
 */
public interface RegistryService {

    /**
     * 注册一个服务实例。
     *
     * @param service  服务名称。
     * @param instance 服务的实例元数据，包含实例的详细信息。
     * @return 注册后的服务实例元数据，可能包含了注册中心分配的ID等信息。
     */
    InstanceMeta register(String service, InstanceMeta instance);

    /**
     * 注销一个服务实例。
     *
     * @param service  服务名称。
     * @param instance 要注销的服务实例元数据。
     * @return 注销后的服务实例元数据，或者如果实例不存在返回null。
     */
    InstanceMeta unregister(String service, InstanceMeta instance);

    /**
     * 查询指定服务的所有实例。
     *
     * @param service 要查询的服务名称。
     * @return 该服务的所有实例元数据列表，如果没有实例则返回空列表。
     */
    List<InstanceMeta> fetchAll(String service);

    /**
     * 更新实例元数据，并关联指定的服务。
     * 同时会更新实例信息 完成心跳
     *
     * @param instance 要更新的实例元数据
     * @param services 要关联的服务名称，可以是多个
     * @return 返回当前服务的版本信息
     */
    Map<String, Long> reNew(InstanceMeta instance, String... services);

    /**
     * 获取指定服务的当前版本号。
     *
     * @param service 要查询版本的服务名称
     * @return 该服务的当前版本号
     */
    Long version(String service);

    /**
     * 获取多个服务的当前版本号。
     *
     * @param service 要查询版本的服务名称列表
     * @return 包含各个服务名称及其对应版本号的映射
     */
    Map<String, Long> versions(String... service);
}
