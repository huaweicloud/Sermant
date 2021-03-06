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
            .maxAttempts(rule.getMaxAttempts())
            .retryOnResult(retryPredicateCreator.createResultPredicate(retry, rule))
            .retryOnException(retryPredicateCreator.createExceptionPredicate(retry.retryExceptions()))
            .intervalFunction(getIntervalFunction(rule))
            .build();
        return Optional.of(RetryRegistry.of(retryConfig).retry(businessName));
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
