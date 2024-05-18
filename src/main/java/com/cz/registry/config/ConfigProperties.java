package com.cz.registry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * code desc
 *
 * @author Zjianru
 */
@Data
@ConfigurationProperties(prefix = "registry")
public class ConfigProperties {
    private List<String> servers;
}
