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

package com.huawei.flowcontrol.retry.handler;

import com.huawei.flowcontrol.common.adapte.cse.resolver.RetryResolver;
import com.huawei.flowcontrol.common.adapte.cse.rule.RetryRule;
import com.huawei.flowcontrol.common.handler.AbstractRequestHandler;
import com.huawei.flowcontrol.common.handler.retry.RetryContext;
import com.huawei.flowcontrol.retry.FeignRequestInterceptor.FeignRetry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.util.Optional;

/**
 * 基于resilience4j重试
 *
 * @author zhouss
 * @since 2022-02-18
 */
public class RetryHandlerV2 extends AbstractRequestHandler<Retry, RetryRule> {
    private final RetryPredicateCreator retryPredicateCreator = new DefaultRetryPredicateCreator();

    @Override
    protected Optional<Retry> createProcessor(String businessName, RetryRule rule) {
        final com.huawei.flowcontrol.common.handler.retry.Retry retry = RetryContext.INSTANCE.getRetry();
        if (retry == null) {
            return Optional.empty();
        }
        final RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(getMaxAttempts(retry, rule))
                .retryOnResult(retryPredicateCreator.createResultPredicate(retry, rule))
                .retryOnException(retryPredicateCreator.createExceptionPredicate(retry.retryExceptions()))
                .intervalFunction(getIntervalFunction(rule))
                .failAfterMaxAttempts(rule.isFailAfterMaxAttempts())
                .build();
        return Optional.of(RetryRegistry.of(retryConfig).retry(businessName));
    }

    /**
     * 获取最大重试次数, 此处做了不同执行方式的最大重试次数修正, 针对拦截的方式由于本身已执行过一次, 因此此处需-1处理,
     * 而目前仅{@link FeignRetry}基于拦截进行重试, 采取-1策略
     *
     * @param retry 重试类型
     * @param rule 规则
     * @return 最大重试次数
     */
    private int getMaxAttempts(com.huawei.flowcontrol.common.handler.retry.Retry retry, RetryRule rule) {
        if (retry instanceof FeignRetry) {
            return rule.getMaxAttempts() - 1;
        }
        return rule.getMaxAttempts();
    }

    @Override
    protected String configKey() {
        return RetryResolver.CONFIG_KEY;
    }

    private IntervalFunction getIntervalFunction(RetryRule rule) {
        if (RetryRule.STRATEGY_RANDOM_BACKOFF.equals(rule.getRetryStrategy())) {
            return IntervalFunction.ofExponentialRandomBackoff(rule.getParsedInitialInterval(),
                    rule.getMultiplier(), rule.getRandomizationFactor());
        }
        return IntervalFunction.of(rule.getParsedWaitDuration());
    }
}
