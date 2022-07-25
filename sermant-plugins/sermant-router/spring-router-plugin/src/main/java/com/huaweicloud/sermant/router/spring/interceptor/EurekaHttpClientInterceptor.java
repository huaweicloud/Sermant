/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.spring.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.spring.cache.AppCache;

import com.netflix.appinfo.InstanceInfo;

import java.util.Optional;

/**
 * EurekaHttpClient增强类，eureka注册方法
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class EurekaHttpClientInterceptor extends AbstractInterceptor {
    private final RouterConfig routerConfig;

    /**
     * 构造方法
     */
    public EurekaHttpClientInterceptor() {
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object argument = context.getArguments()[0];
        if (argument instanceof InstanceInfo) {
            InstanceInfo instanceInfo = (InstanceInfo) argument;
            AppCache.INSTANCE.setAppName(instanceInfo.getAppName());
            instanceInfo.getMetadata().put(RouterConstant.TAG_VERSION_KEY, routerConfig.getRouterVersion());
            Optional.ofNullable(routerConfig.getParameter()).ifPresent(instanceInfo.getMetadata()::putAll);
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }
}