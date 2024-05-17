package com.cz.registry.config;

import com.cz.registry.health.CzHealthChecker;
import com.cz.registry.health.HealthChecker;
import com.cz.registry.service.RegistryService;
import com.cz.registry.service.impl.CzRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * config all bean for registry when application start
 *
 * @author Zjianru
 */
@Configuration
public class InitConfig {

    /**
     * create registry service
     *
     * @return RegistryService
     */
    @Bean
    RegistryService registryService() {
        return new CzRegistryService();
    }

    /**
     * autowire healthChecker
     *
     * @param registryService registry service implementation
     * @return HealthChecker
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    HealthChecker healthChecker(@Autowired RegistryService registryService) {
        return new CzHealthChecker(registryService);
    }
}
