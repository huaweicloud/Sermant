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

import com.huawei.flowcontrol.common.util.StringUtils;

/**
 * 熔断规则
 *
 * @author zhouss
 * @since 2021-11-15
 */
public class CircuitBreakerRule extends AbstractRule {
    /**
     * 基于时间统计的时间窗口类型
     */
    public static final String SLIDE_WINDOW_TIME_TYPE = "time";

    /**
     * 基于请求次数的时间窗口类型
     */
    public static final String SLIDE_WINDOW_COUNT_TYPE = "count";

    /**
     * 默认失败错误率阈值
     */
    public static final float DEFAULT_FAILURE_RATE_THRESHOLD = 50f;

    /**
     * 默认慢调用阈值
     */
    public static final float DEFAULT_SLOW_CALL_RATE_THRESHOLD = 100f;

    /**
     * 默认熔断间隔 60S
     */
    public static final long DEFAULT_WAIT_DURATION_IN_OPEN_STATUS_MS = 60000L;

    /**
     * 慢调用默认阈值 60S
     */
    public static final long DEFAULT_SLOW_CALL_DURATION_THRESHOLD_MS = 60000L;

    /**
     * 半打开状态允许通过的请求数
     */
    public static final int DEFAULT_PERMITTED = 10;

    /**
     * 默认最小调用数
     */
    public static final int DEFAULT_MINIMUM_NUMBER_CALLS = 100;

    /**
     * 默认窗口大小 支持时间与请求数
     */
    public static final long DEFAULT_SLIDING_WINDOW_SIZE = 100L;

    /**
     * 最大比例
     */
    private static final float MAX_PERCENT = 100.0f;

    /**
     * 最小比例
     */
    private static final float MIN_PERCENT = 0.0f;

    /**
     * 错误率，达到该错误率触发熔断
     */
    private float failureRateThreshold = DEFAULT_FAILURE_RATE_THRESHOLD;

    /**
     * 慢调用率
     */
    private float slowCallRateThreshold = DEFAULT_SLOW_CALL_RATE_THRESHOLD;

    /**
     * 熔断后尝试请求间隔
     */
    private String waitDurationInOpenState = String.valueOf(DEFAULT_WAIT_DURATION_IN_OPEN_STATUS_MS);

    /**
     * 转换后的间隔
     */
    private long parsedWaitDurationInOpenState = DEFAULT_WAIT_DURATION_IN_OPEN_STATUS_MS;

    /**
     * 慢调用熔断后请求间隔
     */
    private String slowCallDurationThreshold = String.valueOf(DEFAULT_SLOW_CALL_DURATION_THRESHOLD_MS);

    /**
     * 转换后慢调用熔断后请求间隔
     */
    private long parsedSlowCallDurationThreshold = DEFAULT_SLOW_CALL_DURATION_THRESHOLD_MS;

    /**
     * 半打开状态请求数
     */
    private int permittedNumberOfCallsInHalfOpenState = DEFAULT_PERMITTED;

    /**
     * 最小调用请求基数
     */
    private int minimumNumberOfCalls = DEFAULT_MINIMUM_NUMBER_CALLS;

    /**
     * 滑动窗口类型，请求数（count）与时间（time）
     */
    private String slidingWindowType;

    /**
     * 滑动窗口大小
     */
    private String slidingWindowSize = String.valueOf(DEFAULT_SLIDING_WINDOW_SIZE);

    /**
     * 转换后的滑动窗口大小
     */
    private long parsedSlidingWindowSize = DEFAULT_SLIDING_WINDOW_SIZE;

    /**
     * 强制关闭熔断
     */
    private boolean forceClosed = false;

    /**
     * 强制开启熔断
     */
    private boolean forceOpen = false;

    public boolean isForceClosed() {
        return forceClosed;
    }

    public void setForceClosed(boolean forceClosed) {
        this.forceClosed = forceClosed;
    }

    public boolean isForceOpen() {
        return forceOpen;
    }

    public void setForceOpen(boolean forceOpen) {
        this.forceOpen = forceOpen;
    }

    @Override
    public boolean isInValid() {
        if (failureRateThreshold > MAX_PERCENT || failureRateThreshold <= MIN_PERCENT) {
            return false;
        }
        if (slowCallRateThreshold > MAX_PERCENT || slowCallRateThreshold <= MIN_PERCENT) {
            return false;
        }
        if (parsedWaitDurationInOpenState <= 0 || parsedSlowCallDurationThreshold <= 0) {
            return false;
        }
        if (permittedNumberOfCallsInHalfOpenState <= 0) {
            return false;
        }
        if (minimumNumberOfCalls <= 0) {
            return false;
        }

        return super.isInValid();
    }

    public float getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public void setFailureRateThreshold(float failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    public float getSlowCallRateThreshold() {
        return slowCallRateThreshold;
    }

    public void setSlowCallRateThreshold(float slowCallRateThreshold) {
        this.slowCallRateThreshold = slowCallRateThreshold;
    }

    public String getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }

    /**
     * 设置熔断等待时长
     *
     * @param waitDurationInOpenState 等待时长，该格式是duration格式
     */
    public void setWaitDurationInOpenState(String waitDurationInOpenState) {
        this.waitDurationInOpenState = waitDurationInOpenState;
        this.parsedWaitDurationInOpenState = parseLongTime(waitDurationInOpenState,
            DEFAULT_WAIT_DURATION_IN_OPEN_STATUS_MS);
    }

    public String getSlowCallDurationThreshold() {
        return slowCallDurationThreshold;
    }

    /**
     * 设置慢调用阈值
     *
     * @param slowCallDurationThreshold 慢调用阈值
     */
    public void setSlowCallDurationThreshold(String slowCallDurationThreshold) {
        this.slowCallDurationThreshold = slowCallDurationThreshold;
        this.parsedSlowCallDurationThreshold = parseLongTime(slowCallDurationThreshold,
            DEFAULT_SLOW_CALL_DURATION_THRESHOLD_MS);
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }

    public void setPermittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) {
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    }

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    public String getSlidingWindowType() {
        return slidingWindowType;
    }

    public void setSlidingWindowType(String slidingWindowType) {
        this.slidingWindowType = slidingWindowType;
    }

    public String getSlidingWindowSize() {
        return slidingWindowSize;
    }

    /**
     * 设置时间窗口
     *
     * @param slidingWindowSize 时间窗口值
     */
    public void setSlidingWindowSize(String slidingWindowSize) {
        if (!StringUtils.isEmpty(slidingWindowSize)) {
            this.slidingWindowSize = slidingWindowSize;
            this.parsedSlidingWindowSize = parseLongTime(slidingWindowSize, DEFAULT_SLIDING_WINDOW_SIZE);
        }
    }

    public long getParsedWaitDurationInOpenState() {
        return parsedWaitDurationInOpenState;
    }

    public long getParsedSlowCallDurationThreshold() {
        return parsedSlowCallDurationThreshold;
    }

    public long getParsedSlidingWindowSize() {
        return parsedSlidingWindowSize;
    }
}
