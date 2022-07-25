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

package com.huawei.flowcontrol.common.handler.retry.policy;

/**
 * 同一个实例重试, 不考虑线程安全, 仅作用于线程变量
 *
 * @author zhouss
 * @since 2022-07-25
 */
public class RetryOnSamePolicy implements RetryPolicy {
    private final int retryOnSame;

    private Object lastRetryInstance;

    private int hasTriedCount;

    private boolean isRetry;

    private boolean isFirstMark = true;

    /**
     * 重试构造器
     *
     * @param retryOnSame 针对同一个实例重试的次数
     */
    public RetryOnSamePolicy(int retryOnSame) {
        this.retryOnSame = retryOnSame;
    }

    @Override
    public boolean needRetry() {
        return hasTriedCount < retryOnSame;
    }

    @Override
    public void retryMark() {
        if (!isFirstMark) {
            this.hasTriedCount++;
        }
        this.isRetry = true;
        isFirstMark = false;
    }

    @Override
    public boolean isRetry() {
        return isRetry;
    }

    @Override
    public Object getLastRetryServer() {
        return lastRetryInstance;
    }

    @Override
    public void update(Object instance) {
        this.lastRetryInstance = instance;
    }
}
