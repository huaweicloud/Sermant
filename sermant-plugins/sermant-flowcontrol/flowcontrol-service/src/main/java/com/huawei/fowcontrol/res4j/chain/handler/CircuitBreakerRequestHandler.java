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

import com.huawei.flowcontrol.common.adapte.cse.rule.CircuitBreakerRule;
import com.huawei.fowcontrol.res4j.chain.HandlerConstants;
import com.huawei.fowcontrol.res4j.chain.context.RequestContext;
import com.huawei.fowcontrol.res4j.handler.CircuitBreakerHandler;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 熔断处理
 *
 * @author zhouss
 * @since 2022-07-05
 */
public class CircuitBreakerRequestHandler extends FlowControlHandler<CircuitBreaker> {
    private static final String CONTEXT_NAME = CircuitBreakerRequestHandler.class.getName();

    private static final String START_TIME = CONTEXT_NAME + "_START_TIME";

    private static final String SEPARATOR = "\\|";

    /**
     * 熔断器标志索引(ForceClose){@link CircuitBreakerRule#isForceClosed()} ()}, 位于name基于SEPARATOR分隔数组的最后一个
     * 创建位置参考
     *
     * @see CircuitBreakerHandler#breakerName(String, CircuitBreakerRule)
     */
    private static final int FORCE_CLOSE_LAST_INDEX = 1;

    /**
     * 熔断器标志索引(ForceOpen){@link CircuitBreakerRule#isForceOpen()}, 位于name基于SEPARATOR
     * 分隔数组的倒数第二个
     * 创建位置参考
     *
     * @see CircuitBreakerHandler#breakerName(String, CircuitBreakerRule)
     */
    private static final int FORCE_OPEN_LAST_INDEX = 2;

    private final CircuitBreakerHandler circuitBreakerHandler = getHandler();

    @Override
    public void onBefore(RequestContext context, Set<String> businessNames) {
        final List<CircuitBreaker> circuitBreakers = circuitBreakerHandler.createOrGetHandlers(businessNames);
        if (!circuitBreakers.isEmpty()) {
            circuitBreakers.forEach(CircuitBreaker::acquirePermission);

            // 这里使用内置方法获取时间, 列表中的每个熔断器时间均一致，因此取第一个
            context.save(getStartTime(), circuitBreakers.get(0).getCurrentTimestamp());
            context.save(getContextName(), circuitBreakers);
        }
        super.onBefore(context, businessNames);
    }

    @Override
    public void onThrow(RequestContext context, Set<String> businessNames, Throwable throwable) {
        process(context, throwable, null, false);
        super.onThrow(context, businessNames, throwable);
    }

    @Override
    public void onResult(RequestContext context, Set<String> businessNames, Object result) {
        try {
            process(context, null, result, true);
        } finally {
            context.remove(getContextName());
        }
        super.onResult(context, businessNames, result);
    }

    private void process(RequestContext context, Throwable throwable, Object result, boolean isResult) {
        final Long startTime = context.get(getStartTime(), Long.class);
        final List<CircuitBreaker> circuitBreakers = getHandlersFromCache(context.getSourceName(), getContextName());
        if (startTime == null || circuitBreakers == null || circuitBreakers.isEmpty()) {
            return;
        }
        long duration = circuitBreakers.get(0).getCurrentTimestamp() - startTime;
        final TimeUnit timestampUnit = circuitBreakers.get(0).getTimestampUnit();
        if (throwable != null) {
            circuitBreakers.forEach(circuitBreaker -> circuitBreaker.onError(duration, timestampUnit, throwable));
        }
        if (isResult) {
            circuitBreakers.forEach(circuitBreaker -> circuitBreaker.onResult(duration, timestampUnit, result));
        }
    }

    @Override
    protected boolean isSkip(RequestContext context, Set<String> businessNames) {
        return checkState(circuitBreakerHandler.createOrGetHandlers(businessNames));
    }

    private boolean checkState(List<CircuitBreaker> circuitBreakers) {
        for (CircuitBreaker circuitBreaker : circuitBreakers) {
            final String name = circuitBreaker.getName();
            final String[] parts = name.split(SEPARATOR);
            if (Boolean.parseBoolean(parts[FORCE_CLOSE_LAST_INDEX])) {
                // 强制关闭则跳过当前处理器逻辑
                return true;
            }
            if (Boolean.parseBoolean(parts[FORCE_OPEN_LAST_INDEX])) {
                // 强制开启则直接抛出异常
                throw CallNotPermittedException.createCallNotPermittedException(circuitBreaker);
            }
        }
        return false;
    }

    /**
     * 获取当前缓存上下文名称
     *
     * @return 缓存上下文名称
     */
    @Override
    protected String getContextName() {
        return CONTEXT_NAME;
    }

    /**
     * 获取当前缓存上下文开始时间
     *
     * @return 缓存上下文开始时间
     */
    protected String getStartTime() {
        return START_TIME;
    }

    @Override
    public int getOrder() {
        return HandlerConstants.CIRCUIT_BREAKER_ORDER;
    }

    /**
     * 获取控制器
     *
     * @return 控制器
     */
    protected CircuitBreakerHandler getHandler() {
        return new CircuitBreakerHandler();
    }
}
