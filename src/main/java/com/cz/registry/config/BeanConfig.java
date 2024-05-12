package com.cz.registry.config;

import com.cz.registry.health.CzHealthChecker;
import com.cz.registry.health.HealthChecker;
import com.cz.registry.service.RegistryService;
import com.cz.registry.service.impl.CzRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * config all bean for registry
 *
 * @author Zjianru
 */
@Configuration
public class BeanConfig {

    @Bean
    RegistryService registryService(){
        return new CzRegistryService();
    }
    @Bean(initMethod = "start", destroyMethod = "stop")
    HealthChecker healthChecker(@Autowired RegistryService registryService){
        return new CzHealthChecker(registryService);
    }
}
