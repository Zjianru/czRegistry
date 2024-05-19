package com.cz.registry.util;

import com.cz.registry.cluster.Cluster;
import com.cz.registry.exception.RegistryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * registry utils
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

    /**
     * 检查字符串是否 包含192.168.31.151 有则进行替换
     *
     * @param ipAddress ip地址
     * @return 替换后的ip地址
     */
    public static String convertUrl(String ipAddress) {
        if (ipAddress.contains("192.168.31.151")) {
            ipAddress = ipAddress.replace("192.168.31.151", "127.0.0.1");
        }
        return ipAddress;
    }
}
