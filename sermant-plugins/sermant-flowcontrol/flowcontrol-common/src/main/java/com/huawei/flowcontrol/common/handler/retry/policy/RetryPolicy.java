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
 * 重试策略
 *
 * @author zhouss
 * @since 2022-07-25
 */
public interface RetryPolicy {

    /**
     * 是否需要重试
     *
     * @return 是否重试
     */
    boolean needRetry();

    /**
     * 重试标记
     */
    void retryMark();

    /**
     * 当前是否处于重试状态
     *
     * @return 是否为重试状态
     */
    boolean isRetry();

    /**
     * 获取上一个重试的实例
     *
     * @return 重试实例
     */
    Object getLastRetryServer();

    /**
     * 更新重试实例
     *
     * @param instance 实例
     */
    void update(Object instance);
}
