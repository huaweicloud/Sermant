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

package com.huaweicloud.sermant.core.service.httpserver.config;

import com.huaweicloud.sermant.core.config.common.BaseConfig;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;

/**
 * Http Server 配置
 *
 * @author zwmagic
 * @since 2024-02-01
 */
@ConfigTypeKey("httpserver")
public class HttpServerConfig implements BaseConfig {

    /**
     * HTTP Server 类型
     */
    private String type = "simple";

    /**
     * 默认端口号
     */
    private int port = 47128;
    /**
     * 允许远程访问开关
     */
    private boolean remoteCallLimitEnable = true;

    /**
     * HTTP Server 核心线程池
     */
    private Integer serverCorePoolSize;

    /**
     * HTTP Server 最大线程池
     */
    private Integer serverMaxPoolSize;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isRemoteCallLimitEnable() {
        return remoteCallLimitEnable;
    }

    public void setRemoteCallLimitEnable(boolean remoteCallLimitEnable) {
        this.remoteCallLimitEnable = remoteCallLimitEnable;
    }

    public Integer getServerCorePoolSize() {
        return serverCorePoolSize;
    }

    public void setServerCorePoolSize(Integer serverCorePoolSize) {
        this.serverCorePoolSize = serverCorePoolSize;
    }

    public Integer getServerMaxPoolSize() {
        return serverMaxPoolSize;
    }

    public void setServerMaxPoolSize(Integer serverMaxPoolSize) {
        this.serverMaxPoolSize = serverMaxPoolSize;
    }
}
