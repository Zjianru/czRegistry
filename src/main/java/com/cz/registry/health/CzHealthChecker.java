package com.cz.registry.health;

import com.cz.registry.service.RegistryService;

/**
 * 默认的 health checker
 *
 * @author Zjianru
 */
public class CzHealthChecker extends DefaultHealthChecker{
    public CzHealthChecker(RegistryService registryService) {
        super(registryService);
    }
}
