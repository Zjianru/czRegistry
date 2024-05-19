package com.cz.registry.util;

import com.cz.registry.cluster.Cluster;
import com.cz.registry.exception.RegistryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * code desc
 *
 * @author Zjianru
 */
@Component
public class RegistryUtils {
    @Autowired
    Cluster cluster;

    public void checkMaster() {
        if (!cluster.self().isMaster()) {
            throw new RegistryException("update failed ! current node not master ==> current master is " + cluster.getMaster());
        }
    }

}
