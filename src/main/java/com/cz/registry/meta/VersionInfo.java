package com.cz.registry.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 记录注册实例的版本相关信息
 *
 * @author Zjianru
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfo {
    Long instanceVersion;
    Long timeStamp;
}
