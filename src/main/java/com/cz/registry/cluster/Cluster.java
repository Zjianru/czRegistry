package com.cz.registry.cluster;

import com.cz.registry.cluster.connect.Channel;
import com.cz.registry.config.ConfigProperties;
import com.cz.registry.meta.Server;
import com.cz.registry.service.impl.CzRegistryService;
import com.cz.registry.util.RegistryUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * registry cluster
 *
 * @author Zjianru
 */
@Slf4j
public class Cluster {

    @Value("${server.port}")
    String port;

    String host;

    Server MY_SELF;

    ConfigProperties configProperties;

    Channel channel;

    public Cluster(ConfigProperties configProperties, Channel channel) {
        this.configProperties = configProperties;
        this.channel = channel;
    }

    @Getter
    private List<Server> servers;

    /**
     * 初始化服务器列表。
     * 此方法遍历配置属性中的注册服务器列表，并为每个服务器信息创建一个新的服务器对象，
     * 然后将这个服务器对象添加到服务器列表中。
     * 这个过程中，每个服务器的初始状态为非活动状态（status为false）、非主服务器（isMaster为false），
     * 版本号设置为-1L。
     */
    private void init() {
        // 初始化当前节点
        initSelfServer();
        // 初始化集群
        initServers();
        // 创建一个定时任务线程池，用于定期检查服务器的健康状态
        new ServerHealth(this, channel).process();
    }

    /**
     * 初始化当前节点
     */
    private void initSelfServer() {
        host = RegistryUtils.convertUrl(new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress());
        MY_SELF = Server.getActiveInstance("http://" + host + ":" + port);
        log.info("init self server:{}" , MY_SELF);
    }

    /**
     * 初始化集群
     */
    private void initServers() {
        List<Server> servers = new ArrayList<>();
        // 遍历配置中提供的注册服务器信息，并初始化每个服务器信息
        configProperties.getServers().forEach(serverInfo -> {
            if (serverInfo.equals(MY_SELF.getUrl())) {
                servers.add(MY_SELF);
            } else {
                Server server = Server.getInstance(serverInfo);
                servers.add(server); // 将新建的服务器实例添加到服务器列表中
            }
        });
        this.servers = servers.stream().distinct().toList();
        log.info("init servers:{}" , this.servers);
    }

    /**
     * 获取当前系统中的主服务器。
     * 该方法通过遍历服务器列表，筛选出状态为激活（isStatus）且标记为主服务器（isMaster）的服务器。
     * 如果找到主服务器，则返回该服务器；如果没有找到，则返回null。
     *
     * @return Server 返回系统中的主服务器，如果没有找到则返回null。
     */
    public Server getMaster() {
        return servers.stream()
                .filter(Server::isStatus) // 筛选出状态为激活的服务器
                .filter(Server::isMaster) // 在激活的服务器中筛选出主服务器
                .findFirst() // 获取第一个符合条件的服务器
                .orElse(null); // 如果没有找到符合条件的服务器，则返回null
    }

    /**
     * 获取当前实例代表的服务器。
     * 这是一个系统内部的服务器标识方法，用于获取当前操作的服务器实例。
     *
     * @return Server 返回当前实例代表的服务器。
     */
    public Server self() {
        MY_SELF.setVersion(CzRegistryService.getVersion());
        return MY_SELF; // 返回当前实例代表的服务器
    }

}


