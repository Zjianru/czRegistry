package com.cz.registry;

import com.cz.registry.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class CzRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CzRegistryApplication.class, args);
    }

}
