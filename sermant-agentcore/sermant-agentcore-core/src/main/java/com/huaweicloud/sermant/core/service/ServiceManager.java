/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on org/apache/skywalking/apm/agent/core/boot/ServiceManager.java
 * from the Apache Skywalking project.
 */

package com.huaweicloud.sermant.core.service;

import com.huaweicloud.sermant.core.classloader.ClassLoaderManager;
import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.event.EventManager;
import com.huaweicloud.sermant.core.event.collector.FrameworkEventCollector;
import com.huaweicloud.sermant.core.exception.DupServiceException;
import com.huaweicloud.sermant.core.utils.KeyGenerateUtils;
import com.huaweicloud.sermant.core.utils.SpiLoadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 服务管理器
 *
 * @author justforstudy-A, beetle-man, HapThorin
 * @version 1.0.0
 * @since 2021-10-26
 */
public class ServiceManager {
    /**
     * 动态配置服务类名
     */
    public static final String BUFFERED_DYNAMIC_CONFIG_SERVICE =
            "com.huaweicloud.sermant.implement.service.dynamicconfig.BufferedDynamicConfigService";

    /**
     * 心跳服务类名
     */
    public static final String HEARTBEAT_SERVICE_IMPL =
            "com.huaweicloud.sermant.implement.service.heartbeat.HeartbeatServiceImpl";

    /**
     * 注入服务类名
     */
    public static final String INJECT_SERVICE_IMPL =
            "com.huaweicloud.sermant.implement.service.inject.InjectServiceImpl";

    /**
     * netty网关服务类名
     */
    public static final String NETTY_GATEWAY_CLIENT =
            "com.huaweicloud.sermant.implement.service.send.netty.NettyGatewayClient";

    /**
     * 链路追踪服务类名
     */
    public static final String TRACING_SERVICE_IMPL =
            "com.huaweicloud.sermant.implement.service.tracing.TracingServiceImpl";

    /**
     * HttpServer服务类名
     */
    public static final String HTTP_SERVER_SERVICE_IMPL =
            "com.huaweicloud.sermant.implement.service.httpserver.HttpServerServiceImpl";

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * 服务集合
     */
    private static final Map<String, BaseService> SERVICES = new HashMap<>();

    /**
     * Constructor.
     */
    protected ServiceManager() {
    }

    /**
     * 初始化所有服务
     */
    public static void initServices() {
        ServiceConfig serviceConfig = ConfigManager.getConfig(ServiceConfig.class);
        ArrayList<String> startServiceArray = new ArrayList<>();
        for (final BaseService service : ServiceLoader.load(BaseService.class,
                ClassLoaderManager.getFrameworkClassLoader())) {
            if (serviceConfig.checkServiceEnable(service.getClass().getName()) && loadService(service,
                    service.getClass(), BaseService.class)) {
                service.start();
                startServiceArray.add(service.getClass().getName());
            }
        }
        FrameworkEventCollector.getInstance().collectServiceStartEvent(startServiceArray.toString());
        addStopHook(); // 加载完所有服务再启动服务
    }

    /**
     * 获取服务对象
     *
     * @param serviceClass 服务class
     * @param <T> 服务泛型
     * @return 服务实例对象
     * @throws IllegalArgumentException IllegalArgumentException 找不到对应的服务
     */
    public static <T extends BaseService> T getService(Class<T> serviceClass) {
        String serviceKey = KeyGenerateUtils.generateClassKeyWithClassLoader(serviceClass);
        final BaseService baseService = SERVICES.get(serviceKey);
        if (baseService != null && serviceClass.isAssignableFrom(baseService.getClass())) {
            return (T) baseService;
        }
        throw new IllegalArgumentException("Service instance of [" + serviceClass + "] is not found. ");
    }

    /**
     * 加载服务对象至服务集中
     *
     * @param service 服务对象
     * @param serviceCls 服务class
     * @param baseCls 服务基class，用于spi
     * @return 是否加载成功
     */
    protected static boolean loadService(BaseService service, Class<?> serviceCls,
            Class<? extends BaseService> baseCls) {
        if (serviceCls == null || serviceCls == baseCls || !baseCls.isAssignableFrom(serviceCls)) {
            return false;
        }
        final String serviceKey = KeyGenerateUtils.generateClassKeyWithClassLoader(serviceCls);
        final BaseService oldService = SERVICES.get(serviceKey);
        if (oldService != null && oldService.getClass() == service.getClass()) {
            return false;
        }
        boolean isLoadSucceed = false;
        final BaseService betterService =
                SpiLoadUtils.getBetter(oldService, service, new SpiLoadUtils.WeightEqualHandler<BaseService>() {
                    @Override
                    public BaseService handle(BaseService source, BaseService target) {
                        throw new DupServiceException(serviceKey);
                    }
                });
        if (betterService != oldService) {
            SERVICES.put(serviceKey, service);
            isLoadSucceed = true;
        }
        isLoadSucceed |= loadService(service, serviceCls.getSuperclass(), baseCls);
        for (Class<?> interfaceCls : serviceCls.getInterfaces()) {
            isLoadSucceed |= loadService(service, interfaceCls, baseCls);
        }
        return isLoadSucceed;
    }

    /**
     * 关闭服务
     *
     * @param serviceName 服务名
     */
    protected static void stopService(String serviceName) {
        SERVICES.remove(serviceName).stop();
    }

    /**
     * 添加关闭服务的钩子
     */
    private static void addStopHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            offerEvent();
            for (Map.Entry<String, BaseService> entry : SERVICES.entrySet()) {
                try {
                    entry.getValue().stop();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error occurs while stopping service: " + entry.getKey(), ex);
                }
            }
        }));
    }

    private static void offerEvent() {
        // 上报服务关闭事件
        ArrayList<String> stopServiceArray = new ArrayList<>();
        for (BaseService baseService : new HashSet<>(SERVICES.values())) {
            stopServiceArray.add(baseService.getClass().getName());
        }
        FrameworkEventCollector.getInstance().collectServiceStopEvent(stopServiceArray.toString());

        // 上报Sermant关闭的事件
        FrameworkEventCollector.getInstance().collectAgentStopEvent();
        EventManager.shutdown();
    }

    /**
     * 关闭服务管理器
     */
    public static void shutdown() {
        Set<BaseService> services = new HashSet<>(SERVICES.values());
        for (BaseService baseService : services) {
            baseService.stop();
        }
        SERVICES.clear();
    }
}
