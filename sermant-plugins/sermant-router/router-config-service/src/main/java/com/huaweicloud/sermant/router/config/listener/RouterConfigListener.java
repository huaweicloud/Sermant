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

package com.huaweicloud.sermant.router.config.listener;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;
import com.huaweicloud.sermant.router.config.handler.AbstractHandler;
import com.huaweicloud.sermant.router.config.handler.GlobalConfigHandler;
import com.huaweicloud.sermant.router.config.handler.RouterConfigHandler;
import com.huaweicloud.sermant.router.config.handler.ServiceConfigHandler;
import com.huaweicloud.sermant.router.config.handler.kind.FlowKindHandler;
import com.huaweicloud.sermant.router.config.handler.kind.LaneKindHandler;
import com.huaweicloud.sermant.router.config.handler.kind.TagKindHandler;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 配置监听器
 *
 * @author provenceee
 * @since 2021-11-29
 */
public class RouterConfigListener implements DynamicConfigListener {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final String cacheName;

    private final Set<AbstractHandler> handlers;

    /**
     * 构造方法
     *
     * @param cacheName 缓存的标签名
     */
    public RouterConfigListener(String cacheName) {
        this.cacheName = cacheName;
        this.handlers = new LinkedHashSet<>();
        this.handlers.add(new FlowKindHandler());
        this.handlers.add(new LaneKindHandler());
        this.handlers.add(new TagKindHandler());
        this.handlers.add(new GlobalConfigHandler());
        this.handlers.add(new RouterConfigHandler());
        this.handlers.add(new ServiceConfigHandler());
    }

    @Override
    public void process(DynamicConfigEvent event) {
        String key = event.getKey();
        handlers.forEach(handler -> {
            if (handler.shouldHandle(key)) {
                handler.handle(event, cacheName);
            }
        });
        LOGGER.info(String.format(Locale.ROOT, "Config [%s] has been %s ", key, event.getEventType()));
    }
}