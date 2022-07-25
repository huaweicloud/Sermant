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

package com.huaweicloud.sermant.router.spring.service;

import com.huaweicloud.sermant.core.plugin.service.PluginService;

/**
 * 配置服务
 *
 * @author provenceee
 * @since 2021-11-24
 */
public interface SpringConfigService extends PluginService {
    /**
     * 初始化通知
     *
     * @param cacheName 缓存名
     * @param serviceName 服务名
     */
    void init(String cacheName, String serviceName);

    /**
     * 配置是否无效
     *
     * @param cacheName 缓存名
     * @return 是否无效
     */
    boolean isInValid(String cacheName);
}