package com.cz.registry.config;

import com.cz.registry.service.RegistryService;
import com.cz.registry.service.impl.CzRegistryService;
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
}
