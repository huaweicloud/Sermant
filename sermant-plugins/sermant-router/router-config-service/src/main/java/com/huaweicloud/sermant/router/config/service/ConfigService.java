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

package com.huaweicloud.sermant.router.config.service;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.plugin.subscribe.ConfigSubscriber;
import com.huaweicloud.sermant.core.plugin.subscribe.CseGroupConfigSubscriber;
import com.huaweicloud.sermant.core.plugin.subscribe.DefaultGroupConfigSubscriber;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigServiceType;
import com.huaweicloud.sermant.core.service.dynamicconfig.config.DynamicConfig;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.config.label.LabelCache;
import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.listener.RouterConfigListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 配置服务
 *
 * @author provenceee
 * @since 2022-07-14
 */
public abstract class ConfigService {
    private final AtomicBoolean init = new AtomicBoolean();

    /**
     * 初始化通知
     *
     * @param cacheName 缓存名
     * @param serviceName 服务名
     */
    public void init(String cacheName, String serviceName) {
        if (StringUtils.isBlank(cacheName) || StringUtils.isBlank(serviceName)) {
            return;
        }
        if (init.compareAndSet(false, true)) {
            DynamicConfig dynamicConfig = ConfigManager.getConfig(DynamicConfig.class);
            ConfigSubscriber subscriber;
            RouterConfigListener listener = new RouterConfigListener(cacheName);
            if (dynamicConfig.getServiceType() == DynamicConfigServiceType.KIE) {
                subscriber = new CseGroupConfigSubscriber(serviceName, listener, "Sermant-Route");
            } else {
                subscriber = new DefaultGroupConfigSubscriber(serviceName, listener, "Sermant-Route");
            }
            subscriber.subscribe();
        }
    }

    /**
     * 配置是否无效
     *
     * @param cacheName 缓存名
     * @return 是否无效
     */
    public boolean isInValid(String cacheName) {
        RouterConfiguration configuration = LabelCache.getLabel(cacheName);
        return RouterConfiguration.isInValid(configuration);
    }
}