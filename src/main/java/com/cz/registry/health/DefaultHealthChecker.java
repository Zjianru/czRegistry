package com.cz.registry.health;

import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.service.DefaultRegistryService;
import com.cz.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * code desc
 *
 * @author Zjianru
 */
@Slf4j
public abstract class DefaultHealthChecker implements HealthChecker {

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    RegistryService registryService;

    public DefaultHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    Long timeOut = 20 * 1000L;
    @Override
    public void start() {
        executor.scheduleWithFixedDelay(
                () -> {
                    log.info("czRegistry==> health checker running ");
                    long now = System.currentTimeMillis();
                    Map<String, Long> timestamps = DefaultRegistryService.getAllTimestamps();
                    timestamps.keySet().forEach(serviceAndInstance -> {
                        Long timestamp = timestamps.get(serviceAndInstance);
                        if (now - timestamp > timeOut) {
                            log.info("czRegistry==> serviceAndInstance {} is dead", serviceAndInstance);
                            int index = serviceAndInstance.indexOf("@");
                            String service = serviceAndInstance.substring(0, index);
                            String instanceUtl = serviceAndInstance.substring(index + 1);
                            InstanceMeta instance = InstanceMeta.fromUrl(instanceUtl);
                            registryService.unregister(service, instance);
                            DefaultRegistryService.removeTimestamp(serviceAndInstance);
                        }
                    });
                },
                10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
