package com.cz.registry.health;

import com.cz.registry.service.RegistryService;

/**
 * code desc
 *
 * @author Zjianru
 */
public class CzHealthChecker extends DefaultHealthChecker{
    public CzHealthChecker(RegistryService registryService) {
        super(registryService);
    }
}
