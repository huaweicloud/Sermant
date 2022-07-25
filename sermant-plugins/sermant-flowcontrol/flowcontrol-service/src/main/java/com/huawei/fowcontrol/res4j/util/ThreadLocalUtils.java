/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.fowcontrol.res4j.util;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 本地方法变量, 确保所有线程本地变量归集在一个ThreadLocal, 避免创建太多ThreadLocal
 *
 * @author zhouss
 * @since 2022-07-11
 */
public class ThreadLocalUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final Map<String, Object> LOCAL_MAP = new ConcurrentHashMap<>();

    private static final ThreadLocal<Map<String, Object>> LOCAL = new ThreadLocal<>();

    private ThreadLocalUtils() {
    }

    /**
     * 保存线程变量
     *
     * @param name 变量名称
     * @param target 保存对象
     */
    public static void save(String name, Object target) {
        if (name == null || target == null) {
            LOGGER.warning("ThreadLocal name or target can not be empty!");
            return;
        }
        LOCAL_MAP.put(name, target);
        LOCAL.set(LOCAL_MAP);
    }

    /**
     * 获取线程变量
     *
     * @param name 名称
     * @param <T>  返回类型
     * @return 结果
     */
    public static <T> Optional<T> get(String name) {
        final Map<String, Object> map = LOCAL.get();
        if (map == null) {
            return Optional.empty();
        }
        return (Optional<T>) Optional.ofNullable(map.get(name));
    }

    /**
     * 移除线程变量
     *
     * @param name 变量名称
     */
    public static void remove(String name) {
        final Map<String, Object> map = LOCAL.get();
        if (map == null) {
            return;
        }
        map.remove(name);
        if (map.isEmpty()) {
            LOCAL.remove();
        }
    }
}
