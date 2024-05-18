package com.cz.registry.cluster;

import com.cz.registry.cluster.connect.Channel;
import com.cz.registry.config.ConfigProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cloud.commons.util.InetUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public Cluster(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Getter
    private List<Server> servers;

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    Long timeOut = 5 * 1000L;


    /**
     * 初始化服务器列表。
     * 此方法遍历配置属性中的注册服务器列表，并为每个服务器信息创建一个新的服务器对象，
     * 然后将这个服务器对象添加到服务器列表中。
     * 这个过程中，每个服务器的初始状态为非活动状态（status为false）、非主服务器（isMaster为false），
     * 版本号设置为-1L。
     */
    private void initServers() {
        host = new InetUtils.HostInfo().getIpAddress();
        MY_SELF = Server.getActiveInstance("http://"+host+":"+port);
        log.debug("init self server:{}", MY_SELF);

        List<Server> servers = new ArrayList<>();
        // 遍历配置中提供的注册服务器信息，并初始化每个服务器信息
        configProperties.getServers().forEach(serverInfo -> {
            Server server = Server.getInstance(serverInfo);
            servers.add(server); // 将新建的服务器实例添加到服务器列表中
        });
        this.servers = servers;
        // 开辟线程池, 每隔一段时间执行一次更新服务器状态的任务
        executor.scheduleAtFixedRate(this::updateServers, 0, timeOut, TimeUnit.MILLISECONDS);
    }

    /**
     * 探测刷新注册中心服务器状态
     */
    private void updateServers() {
        servers.forEach(server -> {
            try{
                Server serverInfo = Channel.httpGet(server.getUrl() + "/info", Server.class);
                log.debug(" health check success ==> server info:{}", serverInfo);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setMaster(serverInfo.isMaster());
                    server.setVersion(serverInfo.getVersion());
                }
            }catch (Exception exception){
                log.debug(" health check defeat ==> server info:{}", server);
                server.setStatus(false);
                server.setMaster(false);
            }

        });
    }


    public Server self() {
        return MY_SELF;
    }

}


