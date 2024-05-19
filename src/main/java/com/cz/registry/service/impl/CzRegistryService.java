package com.cz.registry.service.impl;

import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.service.DefaultRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * czRegistry service implements
 *
 * @author Zjianru
 */
@Slf4j
public class CzRegistryService extends DefaultRegistryService {
    /**
     * 注册服务实例。
     *
     * @param service  服务名称
     * @param instance 待注册的服务实例元数据
     * @return 返回注册的实例元数据
     */
    @Override
    public synchronized InstanceMeta register(String service, InstanceMeta instance) {
        return super.register(service, instance);
    }

    /**
     * 注销服务实例。
     *
     * @param service  服务名称
     * @param instance 待注销的服务实例元数据
     * @return 若成功注销返回实例元数据，否则返回null
     */
    @Override
    public synchronized InstanceMeta unregister(String service, InstanceMeta instance) {
        return super.unregister(service, instance);
    }

    /**
     * 获取指定服务的所有实例列表。
     *
     * @param service 服务名称
     * @return 返回服务实例的列表，如果不存在则返回null
     */
    @Override
    public List<InstanceMeta> fetchAll(String service) {
        return super.fetchAll(service);
    }

    /**
     * 更新或刷新服务实例的最新时间戳。
     *
     * @param instance 服务实例元数据
     * @param services 关联的服务名称集合
     */
    @Override
    public synchronized void reNew(InstanceMeta instance, String... services) {
        super.reNew(instance, services);
    }

    /**
     * 获取指定服务的当前版本号。
     *
     * @param service 服务名称
     * @return 返回服务的版本号
     */
    @Override
    public Long version(String service) {
        return super.version(service);
    }

    /**
     * 获取多个服务的当前版本号。
     *
     * @param service 服务名称集合
     * @return 返回一个映射，包含每个服务的版本号
     */
    @Override
    public Map<String, Long> versions(String... service) {
        return super.versions(service);
    }
}
