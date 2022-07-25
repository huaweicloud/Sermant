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

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于ConcurrentMap实现
 *
 * @param <K> 键
 * @param <V> 值
 * @author zhouss
 * @since 2022-07-21
 */
public class ConcurrentMapCache<K, V> implements Cache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    @Override
    public Object getCacheTarget() {
        return cache;
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V evict(K key) {
        return cache.remove(key);
    }

    @Override
    public void release() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }
}
