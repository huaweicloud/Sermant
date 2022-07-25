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

package com.huawei.flowcontrol.common.handler.retry;

import com.huawei.flowcontrol.common.adapte.cse.RuleUtils;
import com.huawei.flowcontrol.common.adapte.cse.resolver.RetryResolver;
import com.huawei.flowcontrol.common.adapte.cse.rule.RetryRule;
import com.huawei.flowcontrol.common.entity.HttpRequestEntity;
import com.huawei.flowcontrol.common.handler.retry.policy.RetryOnSamePolicy;
import com.huawei.flowcontrol.common.handler.retry.policy.RetryPolicy;

import java.util.List;

/**
 * 重试上下文，用于管理重试策略 基于不同的宿主框架类型
 *
 * @author zhouss
 * @since 2022-01-26
 */
public enum RetryContext {
    /**
     * 单例
     */
    INSTANCE;

    private final ThreadLocal<Retry> retryThreadLocal = new ThreadLocal<>();

    private final ThreadLocal<RetryPolicy> policyThreadLocal = new ThreadLocal<>();

    public Retry getRetry() {
        return retryThreadLocal.get();
    }

    /**
     * 标记当前线程重试
     *
     * @param retry 重试器
     */
    public void markRetry(Retry retry) {
        retryThreadLocal.set(retry);
    }

    /**
     * 移除线程变量
     */
    public void remove() {
        retryThreadLocal.remove();
        policyThreadLocal.remove();
    }

    /**
     * 是否标记重试
     *
     * @return 是否标记重试
     */
    public boolean isMarkedRetry() {
        return retryThreadLocal.get() != null;
    }

    /**
     * 当前重试策略是需执行重试
     *
     * @return 是否在重试
     */
    public boolean isPolicyNeedRetry() {
        final RetryPolicy retryPolicy = getRetryPolicy();
        if (retryPolicy == null) {
            return false;
        }
        return retryPolicy.isRetry() && retryPolicy.needRetry();
    }

    public RetryPolicy getRetryPolicy() {
        return policyThreadLocal.get();
    }

    /**
     * 更新重试策略的调用服务实例
     *
     * @param serviceInstance 服务实例
     */
    public void updateServiceInstance(Object serviceInstance) {
        final RetryPolicy retryPolicy = getRetryPolicy();
        if (retryPolicy == null) {
            return;
        }
        retryPolicy.update(serviceInstance);
        retryPolicy.retryMark();
    }

    /**
     * 构建重试策略
     *
     * @param retryRule 重试规则
     */
    public void buildRetryPolicy(RetryRule retryRule) {
        policyThreadLocal.set(new RetryOnSamePolicy(retryRule.getRetryOnSame()));
    }

    /**
     * 构建测试策略
     *
     * @param requestEntity 请求体
     */
    public void buildRetryPolicy(HttpRequestEntity requestEntity) {
        final List<RetryRule> rule = RuleUtils.getRule(requestEntity, RetryResolver.CONFIG_KEY,
                RetryRule.class);
        if (!rule.isEmpty()) {
            RetryContext.INSTANCE.buildRetryPolicy(rule.get(0));
        }
    }
}
