package com.cz.registry.service;

import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.meta.VersionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * registry service template
 *
 * @author Zjianru
 */
@Slf4j
public abstract class DefaultRegistryService implements RegistryService {

    /**
     * 记录已注册的实例信息
     * key->service name
     * value->instance list
     */
    final MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    /**
     * 记录版本信息,服务粒度
     * key->service name
     * value->version info
     */
    final Map<String, VersionInfo> VERSIONS = new ConcurrentHashMap<>();

    final static AtomicLong VERSION = new AtomicLong(0);

    /**
     * 记录能力信息,接口粒度
     * key->service and instance url
     * value->timestamp
     */
    final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();


    /**
     * register instance
     *
     * @param service  service name
     * @param instance register instance
     * @return register instance
     */
    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> instances = REGISTRY.get(service);
        if (instances != null && !instances.isEmpty()) {
            if (instances.contains(instance)) {
                log.info("czRegistry===>instance {} already exists", instance.transferToUrl());
                instance.setStatus(true);
                return instance;
            }
        }
        log.info("czRegistry===>register instance {}", instance.transferToUrl());
        instance.setStatus(true);
        REGISTRY.add(service, instance);
        // process version info
        VERSIONS.put(service, new VersionInfo(VERSION.incrementAndGet(), System.currentTimeMillis()));
        reNew(instance, service);
        return instance;
    }

    /**
     * unregister instance
     *
     * @param service  service name
     * @param instance register instance
     * @return register instance
     */
    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> instances = REGISTRY.get(service);
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        boolean removeIf = instances.removeIf(instance::equals);
        log.info("czRegistry===>unregister instance {}", instance.transferToUrl());
        instance.setStatus(false);
        // process version info
        VERSIONS.put(service, new VersionInfo(VERSION.incrementAndGet(), System.currentTimeMillis()));
        reNew(instance, service);
        // TODO 未找到需要取消注册的实例时,需要给出对应的报错信息
        return removeIf ? instance : null;
    }

    /**
     * get all instances
     *
     * @param service service name
     * @return instances
     */
    @Override
    public List<InstanceMeta> fetchAll(String service) {
        log.info("czRegistry===>fetch all instances of service {}", service);
        return REGISTRY.get(service);
    }

    /**
     * 刷新时间戳和版本信息
     *
     * @param instance register instance
     * @param services services
     */
    @Override
    public void reNew(InstanceMeta instance, String... services) {
        for (String service : services) {
            // process timestamp info
            TIMESTAMPS.put(service + "@" + instance.transferToUrl(), System.currentTimeMillis());
        }
    }

    /**
     * get service version
     *
     * @param service service name
     * @return version
     */
    @Override
    public Long version(String service) {
        return VERSIONS.get(service).getInstanceVersion();
    }

    /**
     * get version by multiple service
     *
     * @param service service name
     * @return service ane version map
     */
    @Override
    public Map<String, Long> versions(String... service) {
        return Arrays.stream(service).collect(Collectors.toMap(k -> k, k -> VERSIONS.get(k).getInstanceVersion()));
    }

    public static Map<String, Long> getAllTimestamps() {
        return TIMESTAMPS;
    }

    public static void removeTimestamp(String serviceAndInstance) {
        TIMESTAMPS.remove(serviceAndInstance);
    }
}
