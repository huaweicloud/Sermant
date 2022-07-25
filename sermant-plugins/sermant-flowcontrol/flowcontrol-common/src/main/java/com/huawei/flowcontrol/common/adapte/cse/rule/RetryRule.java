/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol.common.adapte.cse.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * 重试规则
 *
 * @author zhouss
 * @since 2021-11-15
 */
public class RetryRule extends AbstractRule {
    /**
     * 重试策略
     */
    public static final String STRATEGY_RANDOM_BACKOFF = "RandomBackoff";

    /**
     * 默认最大尝试次数
     */
    public static final int DEFAULT_MAX_ATTEMPTS = 3;

    /**
     * 默认等待下次请求时间
     */
    public static final long DEFAULT_WAIT_DURATION_MS = 10L;

    /**
     * 默认重试状态码
     */
    public static final String DEFAULT_RETRY_ON_RESPONSE_STATUS = "502";

    /**
     * 默认初始基数
     */
    private static final long DEFAULT_INITIAL_INTERVAL_MS = 1000L;

    /**
     * 默认指数
     */
    private static final float DEFAULT_MULTIPLIER = 2f;

    /**
     * 默认随机因子
     */
    private static final double DEFAULT_RANDOMIZATION_FACTOR = 0.5d;

    /**
     * 默认重试策略
     */
    private static final String DEFAULT_RETRY_STRATEGY = "FixedInterval";

    /**
     * 最小基准时间
     */
    private static final long MIN_INITIAL_INTERVAL_MS = 10L;

    /**
     * 最大尝试次数
     */
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

    /**
     * 每次重试尝试等待的时间。
     */
    private String waitDuration = String.valueOf(DEFAULT_WAIT_DURATION_MS);

    /**
     * 转换后的尝试等待时间
     */
    private long parsedWaitDuration = DEFAULT_WAIT_DURATION_MS;

    /**
     * 需要重试的http status, 逗号分隔
     */
    private List<String> retryOnResponseStatus = new ArrayList<>();

    /**
     * 重试策略
     */
    private String retryStrategy = DEFAULT_RETRY_STRATEGY;

    /**
     * 基准时间
     */
    private String initialInterval = String.valueOf(DEFAULT_INITIAL_INTERVAL_MS);

    /**
     * 转换后的基准时间
     */
    private long parsedInitialInterval = DEFAULT_INITIAL_INTERVAL_MS;

    /**
     * 指数基数
     */
    private float multiplier = DEFAULT_MULTIPLIER;

    /**
     * 随机因数
     */
    private double randomizationFactor = DEFAULT_RANDOMIZATION_FACTOR;

    /**
     * 最大重试后失败
     */
    private boolean failAfterMaxAttempts = false;

    /**
     * 是否在在同一台机器重试, 该配置作用到负载均衡, 当前基于agent则采用拦截方式替换
     */
    private int retryOnSame = 0;

    @Override
    public boolean isInValid() {
        if (maxAttempts < 1) {
            return true;
        }
        if (retryOnSame < 0) {
            return true;
        }
        if (parsedWaitDuration < 0) {
            return true;
        }
        if (parsedInitialInterval < MIN_INITIAL_INTERVAL_MS) {
            return true;
        }
        return super.isInValid();
    }

    public boolean isFailAfterMaxAttempts() {
        return failAfterMaxAttempts;
    }

    public void setFailAfterMaxAttempts(boolean failAfterMaxAttempts) {
        this.failAfterMaxAttempts = failAfterMaxAttempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getWaitDuration() {
        return waitDuration;
    }

    public int getRetryOnSame() {
        return retryOnSame;
    }

    public void setRetryOnSame(int retryOnSame) {
        this.retryOnSame = retryOnSame;
    }

    /**
     * 重试等待间隔
     *
     * @param waitDuration 等待间隔
     */
    public void setWaitDuration(String waitDuration) {
        this.waitDuration = waitDuration;
        this.parsedWaitDuration = parseLongTime(waitDuration, DEFAULT_WAIT_DURATION_MS);
    }

    public List<String> getRetryOnResponseStatus() {
        return retryOnResponseStatus;
    }

    public void setRetryOnResponseStatus(List<String> retryOnResponseStatus) {
        this.retryOnResponseStatus = retryOnResponseStatus;
    }

    public String getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(String retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    public String getInitialInterval() {
        return initialInterval;
    }

    /**
     * 基数
     *
     * @param initialInterval 基数
     */
    public void setInitialInterval(String initialInterval) {
        this.initialInterval = initialInterval;
        this.parsedInitialInterval = parseLongTime(initialInterval, DEFAULT_INITIAL_INTERVAL_MS);
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public double getRandomizationFactor() {
        return randomizationFactor;
    }

    public void setRandomizationFactor(double randomizationFactor) {
        this.randomizationFactor = randomizationFactor;
    }

    public long getParsedWaitDuration() {
        return parsedWaitDuration;
    }

    public long getParsedInitialInterval() {
        return parsedInitialInterval;
    }
}
