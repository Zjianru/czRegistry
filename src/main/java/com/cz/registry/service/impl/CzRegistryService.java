package com.cz.registry.service.impl;

import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.service.DefaultRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * czRegistry service implements
 *
 * @author Zjianru
 */
@Slf4j
public class CzRegistryService extends DefaultRegistryService {
    /**
     * register instance
     *
     * @param service service name
     * @param instance    register instance
     * @return register instance
     */
    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        log.info("czRegistry==> current class is {} , current register instance is {}", this.getClass().getName(), instance);
        return super.register(service, instance);
    }

    /**
     * unregister instance
     *
     * @param service service name
     * @param instance    register instance
     * @return register instance
     */
    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        log.info("czRegistry==> current class is {} , current unregister instance is {}", this.getClass().getName(), instance);
        return super.unregister(service, instance);
    }

    /**
     * get all instances
     *
     * @param service service name
     * @return instances
     */
    @Override
    public List<InstanceMeta> fetchAll(String service) {
        log.info("czRegistry==> current class is {} , current fetchAll serviceName is {}", this.getClass().getName(), service);
        return super.fetchAll(service);
    }
}
