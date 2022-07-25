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

package com.huaweicloud.sermant.router.dubbo.service;

import com.huaweicloud.sermant.core.plugin.service.PluginService;

/**
 * ClusterUtils的service
 *
 * @author provenceee
 * @since 2022-03-09
 */
public interface ClusterService extends PluginService {
    /**
     * 从url中缓存接口与下游服务名的映射关系，从map中删除灰度发布相关的参数
     *
     * @param arguments 请求参数
     */
    void doBefore(Object[] arguments);
}