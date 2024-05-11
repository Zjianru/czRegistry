package com.cz.registry.service;

import com.cz.registry.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * registry service template
 *
 * @author Zjianru
 */
@Slf4j
public abstract class DefaultRegistryService implements RegistryService {

    final MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

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
}
