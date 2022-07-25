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

package com.huaweicloud.sermant.router.dubbo.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo缓存
 *
 * @author provenceee
 * @since 2021-11-03
 */
public enum DubboCache {
    /**
     * 实例
     */
    INSTANCE;

    // dubbo应用名
    private String appName;

    private final Map<String, String> applicationCache;

    DubboCache() {
        applicationCache = new ConcurrentHashMap<>();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 缓存接口与服务名的关系
     *
     * @param interfaceName 接口名
     * @param application 服务名
     */
    public void putApplication(String interfaceName, String application) {
        applicationCache.put(interfaceName, application);
    }

    /**
     * 获取应用名
     *
     * @param serviceInterface 接口
     * @return 应用名
     */
    public String getApplication(String serviceInterface) {
        return applicationCache.get(serviceInterface);
    }
}