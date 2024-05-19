package com.cz.registry.cluster;

import com.cz.registry.cluster.connect.Channel;
import com.cz.registry.config.ConfigProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

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
    Channel channel;

    public Cluster(ConfigProperties configProperties, Channel channel) {
        this.configProperties = configProperties;
        this.channel = channel;
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
        host = convertUrl(new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress());
        MY_SELF = Server.getActiveInstance("http://" + host + ":" + port);
        System.out.println("init self server:" + MY_SELF);

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
        System.out.println("init servers:" + this.servers);
        // 开辟线程池, 每隔一段时间执行一次更新服务器状态的任务
        executor.scheduleAtFixedRate(() -> {
            try {
                System.out.println("----update servers status----");
                updateServers();
                System.out.println("----elect master----");
                electMaster();
                // 如果当前服务器不是主服务器并且版本号小于主服务器的版本号，则开始向主服务器进行对准
                if (!MY_SELF.isMaster() && MY_SELF.getVersion() < getMaster().getVersion()){
                    syncSnapshot();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, timeOut, TimeUnit.MILLISECONDS);
    }


    /**
     * 选择一个主服务器。
     * 此方法通过检查当前服务器列表来决定是否需要进行主服务器的选举。
     * 如果没有主服务器或者存在多个主服务器，将触发选举过程。
     * 该方法不接受参数，也不返回任何值。
     */
    private void electMaster() {
        // 过滤出状态为活动并且是主服务器的列表
        List<Server> masters = servers.stream().filter(Server::isStatus).filter(Server::isMaster).toList();

        if (masters.isEmpty()) {
            // 如果没有主服务器，进行选举
            log.warn("no master node,elect master in function [elect master]");
            elect();
        } else if (masters.size() > 1) {
            // 如果有多个主服务器，进行选举
            log.warn("more than one master node,elect master in function [elect master]");
            elect();
        } else {
            // 如果有一个主服务器，不进行选举，记录当前主服务器信息
            log.debug("no need  elect master in function [elect master] current master node is ==>{}", masters.get(0));
        }
    }

    /**
     * 进行主服务器选举。
     * 此方法遍历服务器列表，选择第一个状态为激活（isStatus()返回true）的服务器作为主服务器（master）。
     * 如果存在多个激活状态的服务器，选择hashCode较小的服务器作为主服务器。
     * 选中的服务器将被设置为master状态，并记录选举成功的信息。
     */
    private void elect() {
        Server candidate = null; // 初始候选服务器为空
        for (Server server : servers) {
            server.setMaster(false); // 将当前遍历到的服务器设置为非主服务器
            if (server.isStatus()) { // 仅当服务器状态为激活时考虑将其作为候选服务器
                if (candidate == null) {
                    candidate = server; // 如果当前还没有候选服务器，则将当前服务器设为候选
                } else {
                    if (candidate.hashCode() > server.hashCode()) {
                        candidate = server; // 如果已有候选服务器，且当前服务器的hashCode更小，则更新候选服务器
                    }
                }
            }
        }
        if (candidate != null) {
            candidate.setMaster(true); // 如果找到了候选服务器，则将其设置为主服务器
            log.info("elect master success ==>{}", candidate); // 记录选举成功的信息
        } else {
            log.info("no server is active,elect master defeat");
        }
    }

    /**
     * 探测并刷新注册中心服务器的状态信息。
     * 该方法会并行遍历服务器列表，对除自身外的每台服务器进行健康检查，更新其状态、是否为主服务器以及版本信息。
     */
    private void updateServers() {
        log.debug("start update servers");
        servers.parallelStream() // 使用并行流遍历服务器列表以提高效率
                // 过滤掉当前服务器自身
                .filter(server -> !server.equals(MY_SELF))
                .forEach(server -> {
                    try {
                        // 尝试从服务器的URL获取服务器信息
                        Server serverInfo = channel.get(server.getUrl() + "/info", Server.class);
                        log.debug(" ===>>> health check success for {}", serverInfo);
                        if (serverInfo != null) {
                            // 更新服务器状态为健康，设置为主服务器或从服务器状态，并更新版本信息
                            server.setStatus(true);
                            server.setMaster(serverInfo.isMaster());
                            server.setVersion(serverInfo.getVersion());
                        }
                    } catch (Exception ex) {
                        // 如果健康检查失败，则将服务器状态标记为不健康，并设置为非主服务器
                        log.debug(" ===>>> health check failed for {}", server);
                        server.setStatus(false);
                        server.setMaster(false);
                    }
                });
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
        return MY_SELF; // 返回当前实例代表的服务器
    }


    /**
     * 检查字符串是否 包含192.168.31.151 有则进行替换
     *
     * @param ipAddress ip地址
     * @return 替换后的ip地址
     */
    private String convertUrl(String ipAddress) {
        if (ipAddress.contains("192.168.31.151")) {
            ipAddress = ipAddress.replace("192.168.31.151", "127.0.0.1");
        }
        return ipAddress;
    }


    private void syncSnapshot() {
        Server master = getMaster();
    }


}


