/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.common.config;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;

import java.util.Map;

/**
 * 路由配置
 *
 * @author provenceee
 * @since 2021-11-18
 */
@ConfigTypeKey("router.plugin")
public class RouterConfig implements PluginConfig {
    /**
     * 路由版本
     */
    private String routerVersion = RouterConstant.ROUTER_DEFAULT_VERSION;

    /**
     * ldc
     */
    private String ldc = RouterConstant.ROUTER_DEFAULT_LDC;

    /**
     * 其它配置
     */
    private Map<String, String> parameter;

    /**
     * 构造方法
     */
    public RouterConfig() {
        ServiceMeta serviceMeta = ConfigManager.getConfig(ServiceMeta.class);
        if (serviceMeta == null) {
            return;
        }
        this.routerVersion = serviceMeta.getVersion();
        this.parameter = serviceMeta.getParameter();
    }

    public String getRouterVersion() {
        return routerVersion;
    }

    /**
     * 获取版本
     *
     * @param defaultVersion 默认版本
     * @return 版本
     */
    public String getRouterVersion(String defaultVersion) {
        return routerVersion == null || routerVersion.isEmpty() ? defaultVersion : routerVersion;
    }

    public void setRouterVersion(String routerVersion) {
        this.routerVersion = routerVersion;
    }

    public String getLdc() {
        return ldc;
    }

    /**
     * 获取灰度ldc
     *
     * @param defaultLdc 默认ldc
     * @return ldc
     */
    public String getLdc(String defaultLdc) {
        return ldc == null || ldc.isEmpty() ? defaultLdc : ldc;
    }

    public void setLdc(String ldc) {
        this.ldc = ldc;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }
}