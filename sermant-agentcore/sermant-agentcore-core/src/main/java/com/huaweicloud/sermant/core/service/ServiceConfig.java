/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.core.service;

import com.huaweicloud.sermant.core.config.common.BaseConfig;
import com.huaweicloud.sermant.core.config.common.ConfigFieldKey;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;

/**
 * Agent核心服务的通用配置
 *
 * @author luanwenfei
 * @since 2023-04-01
 */
@ConfigTypeKey("agent.service")
public class ServiceConfig implements BaseConfig {
    @ConfigFieldKey("heartbeat.enable")
    private boolean heartBeatEnable = false;

    @ConfigFieldKey("gateway.enable")
    private boolean gatewayEnable = false;

    @ConfigFieldKey("tracing.enable")
    private boolean tracingEnable = false;

    @ConfigFieldKey("inject.enable")
    private boolean injectEnable = false;

    @ConfigFieldKey("dynamic.config.enable")
    private boolean dynamicConfigEnable = false;

    @ConfigFieldKey("httpserver.enable")
    private boolean httpserverEnable = false;

    public boolean isHeartBeatEnable() {
        return heartBeatEnable;
    }

    public void setHeartBeatEnable(boolean heartBeatEnable) {
        this.heartBeatEnable = heartBeatEnable;
    }

    public boolean isGatewayEnable() {
        return gatewayEnable;
    }

    public void setGatewayEnable(boolean gatewayEnable) {
        this.gatewayEnable = gatewayEnable;
    }

    public boolean isTracingEnable() {
        return tracingEnable;
    }

    public void setTracingEnable(boolean tracingEnable) {
        this.tracingEnable = tracingEnable;
    }

    public boolean isInjectEnable() {
        return injectEnable;
    }

    public void setInjectEnable(boolean injectEnable) {
        this.injectEnable = injectEnable;
    }

    public boolean isDynamicConfigEnable() {
        return dynamicConfigEnable;
    }

    public void setDynamicConfigEnable(boolean dynamicConfigEnable) {
        this.dynamicConfigEnable = dynamicConfigEnable;
    }

    public boolean isHttpserverEnable() {
        return httpserverEnable;
    }

    public void setHttpserverEnable(boolean httpserverEnable) {
        this.httpserverEnable = httpserverEnable;
    }

    /**
     * 通过服务的类名来检查该类型服务是否开启
     *
     * @param serviceName 服务名
     * @return 是否开启了该服务
     */
    public boolean checkServiceEnable(String serviceName) {
        if (ServiceManager.HEARTBEAT_SERVICE_IMPL.equals(serviceName)) {
            return isHeartBeatEnable();
        }
        if (ServiceManager.NETTY_GATEWAY_CLIENT.equals(serviceName)) {
            return isGatewayEnable();
        }
        if (ServiceManager.BUFFERED_DYNAMIC_CONFIG_SERVICE.equals(serviceName)) {
            return isDynamicConfigEnable();
        }
        if (ServiceManager.TRACING_SERVICE_IMPL.equals(serviceName)) {
            return isTracingEnable();
        }
        if (ServiceManager.INJECT_SERVICE_IMPL.equals(serviceName)) {
            return isInjectEnable();
        }
        if (ServiceManager.HTTP_SERVER_SERVICE_IMPL.equals(serviceName)) {
            return isHttpserverEnable();
        }
        return false;
    }
}
