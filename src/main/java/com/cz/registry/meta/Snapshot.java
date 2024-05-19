package com.cz.registry.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

/**
 * 客户端实例快照信息
 * 注册中心集群主从同步时传递
 *
 * @author Zjianru
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot {
    /**
     * 记录已注册的实例信息
     * key->service name
     * value->instance list
     */
    LinkedMultiValueMap<String, InstanceMeta> registry;

    /**
     * 记录版本信息,服务粒度
     * key->service name
     * value->version info
     */
    Map<String, VersionInfo> versions;

    /**
     * 记录版本信息
     */
    Long version;

    /**
     * 记录能力信息,接口粒度
     * key->service and instance url
     * value->timestamp
     */
    Map<String, Long> timeStamps;

}
