/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.implement.service.httpserver;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.service.BaseService;
import com.huaweicloud.sermant.core.service.httpserver.HttpServerService;
import com.huaweicloud.sermant.core.service.httpserver.config.HttpServerConfig;
import com.huaweicloud.sermant.core.utils.SpiLoadUtils;
import com.huaweicloud.sermant.implement.service.httpserver.common.HttpServerTypeEnum;

import org.kohsuke.MetaInfServices;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * httpserver服务实现
 *
 * @author zwmagic
 * @since 2024-02-02
 */
@MetaInfServices(BaseService.class)
public class HttpServerServiceImpl implements HttpServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private HttpServerProvider httpServerProvider;

    @Override
    public void start() {
        Map<String, HttpServerProvider> providerMap = SpiLoadUtils.loadAll(HttpServerProvider.class,
                        this.getClass().getClassLoader()).stream()
                .collect(Collectors.toMap(HttpServerProvider::getType, provider -> provider));
        this.httpServerProvider = providerMap.get(ConfigManager.getConfig(HttpServerConfig.class).getType());
        if (this.httpServerProvider == null) {
            this.httpServerProvider = providerMap.get(HttpServerTypeEnum.SIMPLE.getType());
            LOGGER.warning("can not find httpserver provider, use simple httpserver provider");
        }
        try {
            this.httpServerProvider.start();
        } catch (Exception e) {
            LOGGER.warning("HttpServerService start failed, " + e.getMessage());
            throw new RuntimeException(e);
        }
        LOGGER.info("HttpServerService started.");
    }

    @Override
    public void stop() {
        if (this.httpServerProvider == null) {
            return;
        }
        try {
            this.httpServerProvider.stop();
        } catch (Exception e) {
            LOGGER.warning("HttpServerService stop failed, " + e.getMessage());
            throw new RuntimeException(e);
        }
        LOGGER.info("HttpServerService stopped.");
    }
}
