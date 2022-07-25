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

package com.huawei.fowcontrol.res4j.chain.context;

import com.huawei.fowcontrol.res4j.chain.HandlerConstants;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器上下文
 *
 * @author zhouss
 * @since 2022-07-11
 */
public class ChainContext {
    private static final ThreadLocal<Map<String, RequestContext>> THREAD_LOCAL_CONTEXT_MAP = new ThreadLocal<>();

    private static final int MAX_SIZE = 4;

    private ChainContext() {
    }

    /**
     * 获取指定名称的线程变量缓存
     *
     * @param name 名称
     * @return ThreadLocalContext
     * @throws IllegalArgumentException 当现场变量超过最大size抛出异常
     */
    public static RequestContext getThreadLocalContext(String name) {
        Map<String, RequestContext> contextMap = THREAD_LOCAL_CONTEXT_MAP.get();
        if (contextMap == null) {
            synchronized (ChainContext.class) {
                contextMap = new ConcurrentHashMap<>(MAX_SIZE);
                THREAD_LOCAL_CONTEXT_MAP.set(contextMap);
            }
        }
        if (contextMap.size() >= MAX_SIZE) {
            throw new IllegalArgumentException("Can not create context in current thread!");
        }
        final RequestContext context = contextMap
                .getOrDefault(name, new RequestContext(THREAD_LOCAL_CONTEXT_MAP, name));
        contextMap.put(name, context);
        return context;
    }

    /**
     * 清除线程变量
     */
    public static void remove() {
        final Map<String, RequestContext> contextMap = THREAD_LOCAL_CONTEXT_MAP.get();
        if (contextMap != null) {
            contextMap.clear();
            THREAD_LOCAL_CONTEXT_MAP.remove();
        }
    }

    /**
     * 移除指定缓存
     *
     * @param name 名称
     */
    public static void remove(String name) {
        final Map<String, RequestContext> contextMap = THREAD_LOCAL_CONTEXT_MAP.get();
        if (contextMap != null) {
            contextMap.remove(name);
            if (contextMap.isEmpty()) {
                THREAD_LOCAL_CONTEXT_MAP.remove();
            }
        }
    }

    /**
     * 针对线程变量添加前置
     * <p>
     * {@link RequestContext#get(String, Class)}
     * {@link RequestContext#save(String, Object)}
     * {@link RequestContext#remove(String)}
     * </p>
     *
     * @param sourceName 发起源
     * @param keyPrefix 前缀
     */
    public static void setKeyPrefix(String sourceName, String keyPrefix) {
        if (keyPrefix != null) {
            getThreadLocalContext(sourceName).save(HandlerConstants.THREAD_LOCAL_KEY_PREFIX, keyPrefix);
        }
    }

    /**
     * 获取当前线程变量的key前缀
     *
     * @param sourceName 发起源
     * @return keyPrefix
     */
    public static Optional<String> getKeyPrefix(String sourceName) {
        return Optional.ofNullable(getThreadLocalContext(sourceName)
                .get(HandlerConstants.THREAD_LOCAL_KEY_PREFIX, String.class));
    }
}
