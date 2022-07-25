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

package com.huaweicloud.sermant.router.config.label;

import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 标签缓存
 *
 * @author provenceee
 * @since 2021-10-13
 */
public class LabelCache {
    // 需要刷新标签的缓存
    private static final Map<String, RouterConfiguration> CACHE = new ConcurrentHashMap<>();

    private LabelCache() {
    }

    /**
     * 获取指定标签
     *
     * @param labelName 缓存的标签名
     * @return 标签
     */
    public static RouterConfiguration getLabel(String labelName) {
        RouterConfiguration configuration = CACHE.get(labelName);
        if (configuration == null) {
            synchronized (LabelCache.class) {
                configuration = CACHE.get(labelName);
                if (configuration == null) {
                    CACHE.put(labelName, new RouterConfiguration());
                    configuration = CACHE.get(labelName);
                }
            }
        }
        return configuration;
    }
}