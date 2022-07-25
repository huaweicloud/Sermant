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
import com.huaweicloud.sermant.core.service.ServiceManager;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.ReflectUtils;
import com.huaweicloud.sermant.router.spring.cache.AppCache;
import com.huaweicloud.sermant.router.spring.service.SpringConfigService;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * AbstractAutoServiceRegistration增强类，spring cloud注册方法
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class ServiceRegistryInterceptor extends AbstractInterceptor {
    private final SpringConfigService configService;

    private final RouterConfig routerConfig;

    /**
     * 构造方法
     */
    public ServiceRegistryInterceptor() {
        configService = ServiceManager.getService(SpringConfigService.class);
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object object = context.getObject();
        if (object instanceof AbstractAutoServiceRegistration) {
            AbstractAutoServiceRegistration<?> serviceRegistration = (AbstractAutoServiceRegistration<?>) object;
            Registration registration = (Registration) ReflectUtils
                .getAccessibleObject(serviceRegistration.getClass().getDeclaredMethod("getRegistration"))
                .invoke(serviceRegistration);
            AppCache.INSTANCE.setAppName(registration.getServiceId());
            registration.getMetadata().put(RouterConstant.TAG_VERSION_KEY, routerConfig.getRouterVersion());
            Optional.ofNullable(routerConfig.getParameter()).ifPresent(registration.getMetadata()::putAll);
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        configService.init(RouterConstant.SPRING_CACHE_NAME, AppCache.INSTANCE.getAppName());
        return context;
    }
}