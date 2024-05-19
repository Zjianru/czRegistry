package com.cz.registry.cluster;

import com.cz.registry.meta.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * elect cluster master node
 *
 * @author Zjianru
 */
@Slf4j
public class Election {

    /**
     * 选择一个主服务器。
     * 此方法通过检查当前服务器列表来决定是否需要进行主服务器的选举。
     * 如果没有主服务器或者存在多个主服务器，将触发选举过程。
     * 该方法不接受参数，也不返回任何值。
     */
    public void electMaster(List<Server> servers) {
        // 过滤出状态为活动并且是主服务器的列表
        List<Server> masters = servers.stream().filter(Server::isStatus).filter(Server::isMaster).toList();
        if (masters.isEmpty()) {
            // 如果没有主服务器，进行选举
            log.warn("no master node,elect master in function [elect master]");
            elect(servers);
        } else if (masters.size() > 1) {
            // 如果有多个主服务器，进行选举
            log.warn("more than one master node,elect master in function [elect master]");
            elect(servers);
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
    public void elect(List<Server> servers) {
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
            log.debug("elect master success ==>{}", candidate); // 记录选举成功的信息
        } else {
            log.debug("no server is active,elect master defeat");
        }
    }
}
