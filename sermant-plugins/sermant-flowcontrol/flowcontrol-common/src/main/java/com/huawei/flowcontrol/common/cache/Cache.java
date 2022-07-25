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

package com.huawei.flowcontrol.common.cache;

import java.util.function.Supplier;

/**
 * 缓存
 *
 *
 * @author zhouss
 * @since 2022-07-21
 * @param <K> 键
 * @param <V> 值
 */
public interface Cache<K, V> {
    /**
     * 返回真正缓存的对象
     *
     * @return 缓存对象
     */
    Object getCacheTarget();

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    V get(K key);

    /**
     * 获取值, 若为空使用supplier提供的值返回
     *
     * @param key 键
     * @param supplier 值提供器
     * @return 值
     */
    default V get(K key, Supplier<V> supplier) {
        final V value = get(key);
        if (value == null) {
            return supplier.get();
        }
        return value;
    }

    /**
     * 设定值
     *
     * @param key 键
     * @param value 值
     */
    void put(K key, V value);

    /**
     * 移除缓存key
     *
     * @param key 需要移除的缓存键
     * @return V 返回删除的值
     */
    V evict(K key);

    /**
     * 是否资源
     */
    void release();

    /**
     * 缓存数量
     *
     * @return 缓存数
     */
    int size();
}
