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

package com.huaweicloud.sermant.router.dubbo.service;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.config.label.LabelCache;
import com.huaweicloud.sermant.router.config.label.entity.CurrentTag;
import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.dubbo.cache.DubboCache;
import com.huaweicloud.sermant.router.dubbo.utils.DubboReflectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ApplicationConfig的service
 *
 * @author provenceee
 * @since 2021-11-24
 */
public class ApplicationConfigServiceImpl implements ApplicationConfigService {
    private final RouterConfig routerConfig;

    /**
     * 构造方法
     */
    public ApplicationConfigServiceImpl() {
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
    }

    /**
     * 获取dubbo服务名，并设置灰度参数
     *
     * @param obj 增强的类
     * @see com.alibaba.dubbo.config.ApplicationConfig
     * @see org.apache.dubbo.config.ApplicationConfig
     */
    @Override
    public void getName(Object obj) {
        String name = DubboReflectUtils.getName(obj);
        if (StringUtils.isBlank(name)) {
            return;
        }
        DubboCache.INSTANCE.setAppName(name);
        String version = routerConfig.getRouterVersion(RouterConstant.ROUTER_DEFAULT_VERSION);
        Map<String, String> versionMap = new HashMap<>();
        versionMap.put(RouterConstant.TAG_VERSION_KEY, version);
        String ldc = routerConfig.getLdc(RouterConstant.ROUTER_DEFAULT_LDC);
        versionMap.put(RouterConstant.ROUTER_LDC_KEY, ldc);
        Map<String, String> parameters = DubboReflectUtils.getParameters(obj);
        if (parameters == null) {
            DubboReflectUtils.setParameters(obj, versionMap);
        } else {
            parameters.putAll(versionMap);
        }
        RouterConfiguration configuration = LabelCache.getLabel(RouterConstant.DUBBO_CACHE_NAME);
        CurrentTag currentTag = configuration.getCurrentTag();
        if (currentTag == null) {
            currentTag = new CurrentTag();
        }
        currentTag.setVersion(version);
        currentTag.setLdc(ldc);
        configuration.setCurrentTag(currentTag);
    }
}