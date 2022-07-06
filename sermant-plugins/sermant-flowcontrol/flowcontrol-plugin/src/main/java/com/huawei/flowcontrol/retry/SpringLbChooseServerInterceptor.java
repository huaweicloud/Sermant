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

package com.huawei.flowcontrol.retry;

import com.huawei.flowcontrol.common.handler.retry.RetryContext;
import com.huawei.flowcontrol.common.handler.retry.policy.RetryPolicy;
import com.huawei.flowcontrol.service.InterceptorSupporter;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.ReflectUtils;

import java.util.Optional;

/**
 * springCloud loadbalancer选择实例方法拦截, 通过拦截获取上次调用的服务是哪一个, 从而在重试时根据负载均衡再次选择上次调用的服务
 *
 * @author zhouss
 * @see com.huawei.flowcontrol.common.handler.retry.policy.RetryOnSamePolicy
 * @since 2022-07-25
 */
public class SpringLbChooseServerInterceptor extends InterceptorSupporter {
    private static final String RESPONSE_REACTIVE_CLASS =
            "org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse";

    private static final String RESPONSE_CLASS =
            "org.springframework.cloud.client.loadbalancer.DefaultResponse";

    @Override
    protected ExecuteContext doBefore(ExecuteContext context) throws Exception {
        return context;
    }

    @Override
    protected ExecuteContext doAfter(ExecuteContext context) throws Exception {
        final Object result = context.getResult();
        if (!isTarget(result)) {
            return context;
        }
        if (RetryContext.INSTANCE.isPolicyNeedRetry()) {
            tryChangeServiceInstanceForRetry(result);
        } else {
            updateServiceInstance(result);
        }
        return context;
    }

    private void tryChangeServiceInstanceForRetry(Object result) {
        final RetryPolicy retryPolicy = RetryContext.INSTANCE.getRetryPolicy();
        if (retryPolicy != null && retryPolicy.getLastRetryServer() != null) {
            retryPolicy.retryMark();
            ReflectUtils.setFieldValue(result, "serviceInstance", retryPolicy.getLastRetryServer());
        }
    }

    private void updateServiceInstance(Object result) {
        final Optional<Object> getServer = ReflectUtils.invokeMethod(result, "getServer", null, null);
        if (!getServer.isPresent()) {
            return;
        }
        RetryContext.INSTANCE.updateServiceInstance(getServer.get());
    }

    private boolean isTarget(Object result) {
        final String name = result.getClass().getName();
        return RESPONSE_CLASS.equals(name) || RESPONSE_REACTIVE_CLASS.equals(name);
    }

    @Override
    protected boolean canInvoke(ExecuteContext context) {
        return true;
    }
}
