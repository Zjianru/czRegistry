package com.cz.registry.service;

import com.cz.registry.meta.InstanceMeta;

import java.util.List;

/**
 * interface of registry service
 *
 * @author Zjianru
 */
public interface RegistryService {

    /**
     * register instance
     *
     * @param service service name
     * @param instance    register instance
     * @return register instance
     */
    InstanceMeta register(String service, InstanceMeta instance);

    /**
     * unregister instance
     *
     * @param service service name
     * @param instance    register instance
     * @return register instance
     */
    InstanceMeta unregister(String service, InstanceMeta instance);

    /**
     * get all instances
     *
     * @param service service name
     * @return instances
     */
    List<InstanceMeta> fetchAll(String service);

    // TODO enhance more ability


}
