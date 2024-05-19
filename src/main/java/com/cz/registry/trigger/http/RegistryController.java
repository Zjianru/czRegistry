package com.cz.registry.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.cz.registry.cluster.Cluster;
import com.cz.registry.cluster.Server;
import com.cz.registry.meta.InstanceMeta;
import com.cz.registry.meta.SnapShot;
import com.cz.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * rest controller for registry
 *
 * @author Zjianru
 */
@RestController
@Slf4j
public class RegistryController {

    @Autowired
    RegistryService registryService;

    @Autowired
    Cluster cluster;

    /**
     * 注册服务实例。
     *
     * @param service 要注册的服务名称。
     * @param host    服务实例所在的主机地址。
     * @param port    服务实例监听的端口号。
     * @return InstanceMeta 服务注册后的实例元数据。
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public InstanceMeta register(@RequestParam String service, String host, Integer port) {
        // 记录注册服务的日志信息
        log.info("register service:{} host:{} port:{}", service, host, port);
        // 调用注册服务，返回实例的元数据
        return registryService.register(service, InstanceMeta.http(host, port));
    }

    /**
     * 通过POST请求注册服务实例。
     *
     * @param service  要注册的服务名称。
     * @param instance 需要注册的服务实例信息，以JSON格式通过请求体传入。
     * @return InstanceMeta 注册后的服务实例元数据。
     */
    @RequestMapping(value = "/registerByPost", method = RequestMethod.POST)
    public InstanceMeta registerByPost(@RequestParam String service, @RequestBody InstanceMeta instance) {
        // 记录注册服务的日志
        log.info("register service:{} instance:{}", service, instance);
        // 调用注册服务，返回注册结果
        return registryService.register(service, instance);
    }

    /**
     * 取消注册指定的服务实例。
     *
     * @param service  要取消注册的服务名称。
     * @param instance 需要注册的服务实例信息，以JSON格式通过请求体传入。
     * @return InstanceMeta 该方法返回取消注册的服务实例的元数据。
     */
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance) {
        // 记录取消注册服务的请求信息
        log.info("unregister service:{} instance:{} ", service, instance);
        // 调用注册中心服务，取消注册指定的服务实例
        return registryService.unregister(service, instance);
    }

    /**
     * 请求处理函数，用于获取指定服务的所有实例信息。
     *
     * @param service 需要查询的服务名称。
     * @return 返回一个包含该服务所有实例信息的列表。
     */
    @RequestMapping(value = "/fetchAll", method = RequestMethod.GET)
    public List<InstanceMeta> fetchAll(@RequestParam String service) {
        // 记录请求日志
        log.info("fetchAll service:{}", service);
        // 调用服务注册中心，查询并返回指定服务的所有实例信息
        return registryService.fetchAll(service);
    }

    /**
     * 测试打印实例信息
     *
     * @return instance info json
     */
    @RequestMapping(value = "/printMetaInfo", method = RequestMethod.GET)
    public String printMetaInfo() {
        InstanceMeta instance = InstanceMeta.http("127.0.0.1", 9091)
                .addParams(Map.of("env", "dev", "tag", "RED"));
        String msg = JSON.toJSONString(instance);
        log.info(msg);
        return msg;
    }

    /**
     * 重新注册服务实例。
     * 该方法用于当服务实例需要更新或重新注册时调用，通过接收服务名称和实例元数据，来实现服务的重新注册。
     *
     * @param service  表示需要重新注册的服务名称。
     * @param instance 表示需要重新注册的服务实例的元数据信息，通过RequestBody接收，允许客户端以JSON格式提交。
     *                 方法不返回任何内容，操作结果通过日志记录或直接通过服务注册过程中的反馈来体现。
     */
    @RequestMapping(value = "/reNew", method = RequestMethod.POST)
    public void reNew(@RequestParam String service, @RequestBody InstanceMeta instance) {
        // 记录接收到的重新注册请求，包括服务名称和实例元数据信息。
        log.info("reNew service:{} instance:{}", service, instance);
        // 调用注册服务的reNew方法，以实例和服务名称为参数，执行重新注册操作。
        registryService.reNew(instance, service);
    }

    /**
     * 请求当前服务的版本信息。
     *
     * @param service 需要查询版本的服务名称。
     * @return 返回对应服务的版本号，类型为Long。
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Long version(@RequestParam String service) {
        // 记录请求版本信息的日志
        log.info("version service:{}", service);
        // 通过服务注册中心查询指定服务的版本号
        return registryService.version(service);
    }

    /**
     * 查询指定服务的版本信息。
     *
     * @param service 需要查询版本信息的服务名，多个服务名以逗号分隔。
     * @return 返回一个Map，其中key为服务名，value为该服务的版本号。
     */
    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public Map<String, Long> versions(@RequestParam String service) {
        // 记录请求信息
        log.info("versions service:{}", service);
        // 调用服务注册中心，查询指定服务的版本信息
        return registryService.versions(service.split(","));
    }

    /**
     * 获取当前服务器的信息。
     *
     * <p>该方法没有参数。
     *
     * @return Server 返回当前服务器的信息对象。
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Server info() {
        // 从集群中获取当前服务器的实例
        Server self = cluster.self();
        // 记录调试信息，输出当前服务器的详细信息
        log.debug("czRegistry ==> self:{}", self);
        return self;
    }

    /**
     * 获取集群服务器列表。
     *
     * <p>该方法没有参数，通过调用集群服务获取当前集群中的所有服务器列表。
     *
     * @return 返回一个服务器列表，列表中包含了集群中所有的服务器信息。
     */
    @RequestMapping(value = "/cluster", method = RequestMethod.GET)
    public List<Server> cluster() {
        // 从集群中获取当前所有服务器列表
        List<Server> servers = cluster.getServers();
        // 记录调试信息，输出当前集群服务器列表
        log.debug("czRegistry ==> servers:{}", servers);
        return servers;
    }

    /**
     * 获取当前集群中的主服务器。
     * <p>
     * 该方法不接受任何参数，通过调用集群服务来获取当前的主服务器信息，并将该信息记录在调试日志中。
     * <p>
     *
     * @return Server 返回当前集群中的主服务器对象。
     */
    @RequestMapping(value = "/getMaster", method = RequestMethod.GET)
    public Server getMaster() {
        // 从集群中获取当前主服务器
        Server master = cluster.getMaster();
        // 记录调试信息，输出当前主服务器的信息
        log.debug("czRegistry ==> master:{}", master);
        return master;
    }

    /**
     * 将当前服务器设置为集群中的主服务器。
     * <p>
     * 该方法不接受任何参数，通过设置当前服务器的状态为激活，来尝试将其置为集群中的主服务器，并记录当前的主服务器信息。
     * <p>
     *
     * @return Server 返回当前集群中的主服务器对象。
     */
    @RequestMapping(value = "/setMaster", method = RequestMethod.GET)
    public Server setMaster() {
        cluster.self().setStatus(true);
        // 从集群中获取当前主服务器
        Server master = cluster.getMaster();
        // 记录调试信息
        log.debug("czRegistry ==> master:{}", master);
        log.debug("czRegistry ==> self:{}", cluster.self());
        return master;
    }

    /**
     * 请求处理函数，用于获取当前的快照信息。
     *
     * <p>该函数没有参数，通过调用{@link RegistryService#snapshot()}方法，
     * 从注册服务中获取当前的快照信息，并将其返回给客户端。</p>
     *
     * @return SnapShot 返回当前的快照信息对象。
     */
    @RequestMapping(value = "/snapshot", method = RequestMethod.GET)
    public SnapShot snapshot() {
        return registryService.snapshot();
    }

}
