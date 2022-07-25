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

package com.huaweicloud.sermant.router.common.addr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 地址缓存
 *
 * @author provenceee
 * @since 2021-10-15
 */
public class AddrCache {
    // 注册版本缓存
    private static final Map<String, String> REGISTER_VERSION_CACHE = new ConcurrentHashMap<>();

    private AddrCache() {
    }

    /**
     * 设置注册版本缓存
     *
     * @param addr 地址
     * @param version 注册版本
     */
    public static void setRegisterVersionCache(String addr, String version) {
        REGISTER_VERSION_CACHE.put(addr, version);
    }

    /**
     * 获取注册版本缓存
     *
     * @param addr 地址
     * @return 注册版本
     */
    public static String getRegisterVersionCache(String addr) {
        return REGISTER_VERSION_CACHE.get(addr);
    }
}