/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.fowcontrol.res4j.util;

import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.enums.FlowControlEnum;
import com.huawei.fowcontrol.res4j.exceptions.InstanceIsolationException;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 针对resilience4j拦截异常处理
 *
 * @author zhouss
 * @since 2022-01-22
 */
public class Rest4jExceptionUtils {
    /**
     * 需释放资源的异常类型
     */
    private static final Set<Class<?>> RELEASE_FLOW_CONTROL_EXCEPTIONS =
            new HashSet<>(Arrays.asList(RequestNotPermitted.class, CallNotPermittedException.class,
                    InstanceIsolationException.class));

    private Rest4jExceptionUtils() {
    }

    /**
     * 处理流控异常
     *
     * @param throwable 异常信息
     * @param result 前置返回结果
     */
    public static void handleException(Throwable throwable, FlowControlResult result) {
        if (throwable instanceof RequestNotPermitted) {
            result.setResult(FlowControlEnum.RATE_LIMITED);
        } else if (throwable instanceof CallNotPermittedException) {
            result.setResult(FlowControlEnum.CIRCUIT_BREAKER);
        } else if (throwable instanceof BulkheadFullException) {
            result.setResult(FlowControlEnum.BULKHEAD_FULL);
        } else if (throwable instanceof InstanceIsolationException) {
            result.setResult(FlowControlEnum.INSTANCE_ISOLATION);
        } else {
            return;
        }
    }

    /**
     * 判断是否为流控异常
     *
     * @param throwable 异常类型
     * @return 是否为流控异常
     */
    public static boolean isNeedReleasePermit(Throwable throwable) {
        for (Class<?> clazz : RELEASE_FLOW_CONTROL_EXCEPTIONS) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }
        return false;
    }
}
