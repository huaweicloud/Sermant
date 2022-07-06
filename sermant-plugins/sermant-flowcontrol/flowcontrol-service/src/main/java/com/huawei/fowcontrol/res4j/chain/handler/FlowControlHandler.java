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

package com.huawei.fowcontrol.res4j.chain.handler;

import com.huawei.fowcontrol.res4j.chain.AbstractChainHandler;
import com.huawei.fowcontrol.res4j.chain.context.ChainContext;

import java.util.List;

/**
 * 流控基础处理器
 *
 * @param <T> 对应流控处理单元
 * @author zhouss
 * @since 2022-07-18
 */
public abstract class FlowControlHandler<T> extends AbstractChainHandler {
    /**
     * 获取流控处理单元 {@link io.github.resilience4j.ratelimiter.RateLimiter} {@link io.github.resilience4j.bulkhead.Bulkhead}
     * {@link io.github.resilience4j.circuitbreaker.CircuitBreaker}
     *
     * @param sourceName 发起源
     * @param cacheName 缓存名称
     * @return handlers
     */
    protected List<T> getHandlersFromCache(String sourceName, String cacheName) {
        return ChainContext.getThreadLocalContext(sourceName).get(cacheName, List.class);
    }

    /**
     * 获取上下文缓存名称
     *
     * @return contextName
     */
    protected String getContextName() {
        return getClass().getName();
    }
}
