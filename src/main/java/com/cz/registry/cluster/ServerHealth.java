package com.cz.registry.cluster;

import com.cz.registry.cluster.connect.Channel;
import com.cz.registry.meta.Server;
import com.cz.registry.meta.Snapshot;
import com.cz.registry.service.impl.CzRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * check server health
 *
 * @author Zjianru
 */
@Slf4j
public class ServerHealth {

    Cluster cluster;
    Channel channel;

    public ServerHealth(Cluster cluster, Channel channel) {
        this.cluster = cluster;
        this.channel = channel;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    Long timeOut = 5 * 1000L;

    /**
     * 启动一个定时任务，用于周期性地处理服务器状态更新、主服务器选举和快照同步。
     * 初始延迟为0，之后每隔指定的timeOut时间执行一次任务。
     * 使用线程池来管理任务，确保任务周期性地执行，以维护系统的一致性和稳定性。
     */
    public void process() {
        // 创建一个定时任务，该任务会定期执行更新服务器状态、选举主服务器和同步快照的操作
        executor.scheduleAtFixedRate(() -> {
            try {
                // 1. 更新服务状态
                log.debug("----update servers status----");
                updateServers();
                // 2. 选主
                log.debug("----elect master----");
                electMaster();
                // 3. 同步快照
                log.debug("----sync snapshot----");
                syncSnapshot();
            } catch (Exception e) {
                // 捕获并打印任务执行过程中发生的异常
                e.printStackTrace();
            }
        }, 0, timeOut, TimeUnit.MILLISECONDS); // 指定任务的初始延迟和执行周期
    }

    /**
     * 选举主服务器
     */
    private void electMaster() {
        new Election().electMaster(cluster.getServers());
    }

    /**
     * 探测并刷新注册中心服务器的状态信息。
     * 该方法会并行遍历服务器列表，对除自身外的每台服务器进行健康检查，更新其状态、是否为主服务器以及版本信息。
     */
    private void updateServers() {
        log.debug("start update servers");
        cluster.getServers().parallelStream() // 使用并行流遍历服务器列表以提高效率
                // 过滤掉当前服务器自身
                .filter(server -> !server.equals(cluster.self())).forEach(server -> {
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
     * 同步快照功能。
     * 该方法用于检查当前服务器是否为主服务器，如果不是，并且其版本号小于主服务器的版本号，则从主服务器获取快照并进行对准。
     * 这个过程通过向主服务器请求快照，并使用获取的快照重置本地的状态来完成。
     */
    private void syncSnapshot() {
        Server self = cluster.self();
        Server master = cluster.getMaster();
        // 检查当前服务器是否为主服务器，并对比版本号
        if (!self.isMaster() && self.getVersion() < master.getVersion()) {
            // 记录日志，说明开始进行同步
            log.debug("current server is not master,version is {} ,master version is {}", self.getVersion(), master.getVersion());
            log.debug("start sync from master {}", master);
            // 从主服务器获取快照
            Snapshot masterSnapshot = channel.get(master.getUrl() + "/snapshot", Snapshot.class);
            // 记录获取到的快照，并开始进行状态重置
            log.debug("get master snapshot:{} start RESET ----", masterSnapshot);
            Long resetVersion = CzRegistryService.reset(masterSnapshot);
            log.debug("RESET end,resetVersion:{}", resetVersion);
        }
    }
}
