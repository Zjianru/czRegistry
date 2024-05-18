package com.cz.registry.cluster;

import lombok.*;

/**
 * registry server instance
 *
 * @author Zjianru
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"url"})
public class Server {
    private String url;
    private boolean status;
    private boolean isMaster;
    private long version;

    /**
     * get server instance
     * [not active] [not master] [version is -1]
     *
     * @param serverInfo server info
     * @return server
     */
    public static Server getInstance(String serverInfo) {
        return Server.builder() // 使用Server的builder模式创建新服务器实例
                .url(serverInfo) // 设置服务器的URL
                .status(false) // 设置服务器初始状态为非活动
                .isMaster(false) // 设置服务器为非主服务器
                .version(-1L) // 设置服务器版本为-1L，表示未设置
                .build(); // 构建服务器实例
    }

    /**
     * get server instance
     * [not master] [version is -1]
     *
     * @param serverInfo server info
     * @return server
     */
    public static Server getActiveInstance(String serverInfo) {
        return Server.builder() // 使用Server的builder模式创建新服务器实例
                .url(serverInfo) // 设置服务器的URL
                .status(true) // 设置服务器初始状态为非活动
                .isMaster(false) // 设置服务器为非主服务器
                .version(-1L) // 设置服务器版本为-1L，表示未设置
                .build(); // 构建服务器实例
    }
}
