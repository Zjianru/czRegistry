package com.cz.registry.service;

import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.meta.Snapshot;
import com.cz.registry.meta.VersionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
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
    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    /**
     * 记录版本信息,服务粒度
     * key->service name
     * value->version info
     */
    final static Map<String, VersionInfo> VERSIONS = new ConcurrentHashMap<>();

    /**
     * 注册中心全局版本号
     */
    final static AtomicLong VERSION = new AtomicLong(0);

    /**
     * 记录能力信息,接口粒度
     * key->service and instance url
     * value->timestamp
     */
    final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();

    /**
     * 注册服务实例。
     *
     * @param service  服务名称
     * @param instance 待注册的服务实例元数据
     * @return 返回注册的实例元数据
     */
    @Override
    public synchronized InstanceMeta register(String service, InstanceMeta instance) {
        // 检查服务实例是否已经存在，若存在则更新状态，不存在则添加到注册表
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
        // 更新版本信息并触发重置操作
        VERSIONS.put(service, new VersionInfo(VERSION.incrementAndGet(), System.currentTimeMillis()));
        reNew(instance, service);
        return instance;
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
        List<InstanceMeta> instances = REGISTRY.get(service);
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        boolean removeIf = instances.removeIf(instance::equals);
        log.info("czRegistry===>unregister instance {}", instance.transferToUrl());
        instance.setStatus(false);
        // 更新版本信息并触发重置操作
        VERSIONS.put(service, new VersionInfo(VERSION.incrementAndGet(), System.currentTimeMillis()));
        reNew(instance, service);
        return removeIf ? instance : null;
    }

    /**
     * 获取指定服务的所有实例列表。
     *
     * @param service 服务名称
     * @return 返回服务实例的列表，如果不存在则返回null
     */
    @Override
    public List<InstanceMeta> fetchAll(String service) {
        log.info("czRegistry===>fetch all instances of service {}", service);
        return REGISTRY.get(service);
    }

    /**
     * 更新或刷新服务实例的最新时间戳。
     *
     * @param instance 服务实例元数据
     * @param services 关联的服务名称集合
     */
    @Override
    public synchronized Map<String, Long> reNew(InstanceMeta instance, String... services) {
        // 更新每个服务的时间戳信息
        for (String service : services) {
            TIMESTAMPS.put(service + "@" + instance.transferToUrl(), System.currentTimeMillis());
        }
        return versions(services);
    }

    /**
     * 获取指定服务的当前版本号。
     *
     * @param service 服务名称
     * @return 返回服务的版本号
     */
    @Override
    public Long version(String service) {
        return VERSIONS.get(service).getInstanceVersion();
    }

    /**
     * 获取多个服务的当前版本号。
     *
     * @param service 服务名称集合
     * @return 返回一个映射，包含每个服务的版本号
     */
    @Override
    public Map<String, Long> versions(String... service) {
        // 批量获取服务版本号
        return Arrays.stream(service)
                .collect(
                        Collectors.toMap(k -> k, k -> VERSIONS.get(k).getInstanceVersion())
                );
    }

    /**
     * 获取所有的时间戳信息。
     *
     * @return 返回一个包含所有服务及其实例时间戳的Map，键为服务和实例的标识符，值为时间戳。
     */
    public static Map<String, Long> getAllTimestamps() {
        return TIMESTAMPS;
    }

    /**
     * 移除指定服务和实例的时间戳。
     *
     * @param serviceAndInstance 服务和实例的标识符，格式为"服务名#实例名"。
     */
    public static synchronized void removeTimestamp(String serviceAndInstance) {
        TIMESTAMPS.remove(serviceAndInstance);
    }

    /**
     * 获取当前所有注册服务的快照。
     * todo 可考虑进行深拷贝优化
     *
     * @return 返回一个包含当前所有注册服务的快照对象，包括服务实例信息、版本信息和时间戳信息。
     */
    public static synchronized Snapshot snapshot() {
        // 创建一个新的MultiValueMap来存放服务实例信息，并从REGISTRY中添加所有数据
        LinkedMultiValueMap<String, InstanceMeta> registry = new LinkedMultiValueMap<>();
        registry.addAll(REGISTRY);
        // 创建一个新的HashMap来存放版本信息，并从VERSIONS中复制所有数据
        Map<String, VersionInfo> versions = new HashMap<>(VERSIONS);
        // 创建一个新的HashMap来存放时间戳信息，并从TIMESTAMPS中复制所有数据
        Map<String, Long> timeStamps = new HashMap<>(TIMESTAMPS);
        // 返回一个包含当前所有注册服务信息的SnapShot对象
        return new Snapshot(registry, versions, VERSION.get(), timeStamps);
    }

    /**
     * 重置内部状态到给定的快照。
     * 清除当前状态，并从提供的快照中恢复。
     *
     * @param snapshot 包含要恢复的状态信息的快照对象。
     * @return 返回快照中的版本号。
     */
    public static synchronized Long reset(Snapshot snapshot) {
        // 日志记录开始重置过程
        log.debug("reset REGISTRY...");
        // 清除当前注册表并从快照恢复
        REGISTRY.clear();
        REGISTRY.addAll(snapshot.getRegistry());

        log.debug("reset VERSIONS...");
        // 清除当前版本信息并从快照恢复
        VERSIONS.clear();
        VERSIONS.putAll(snapshot.getVersions());

        log.debug("reset TIMESTAMPS...");
        // 清除当前时间戳并从快照恢复
        TIMESTAMPS.clear();
        TIMESTAMPS.putAll(snapshot.getTimeStamps());

        log.debug("reset VERSION...");
        // 设置当前版本到快照的版本
        Long version = snapshot.getVersion();
        VERSION.set(version);

        // 日志记录重置完成
        log.debug("finish RESET...");
        // 返回恢复的版本号
        return version;
    }


    /**
     * 获取当前版本的版本号。
     *
     * @return 返回当前版本的版本号。返回值为Long类型，表示版本号的值。
     */
    public static synchronized Long getVersion() {
        return VERSION.get();
    }
}
