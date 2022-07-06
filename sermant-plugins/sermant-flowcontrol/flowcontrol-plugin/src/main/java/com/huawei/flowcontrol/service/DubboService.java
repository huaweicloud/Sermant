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

package com.huawei.flowcontrol.service;

import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.entity.RequestEntity;

/**
 * dubbo拦截
 *
 * @author zhouss
 * @since 2022-01-25
 */
public interface DubboService {
    /**
     * 前置拦截
     *
     * @param sourceName 发起原, 建议为目标拦截器权限定名, 该值用于在跨多个拦截器时区分线程变量
     * @param requestEntity 请求信息
     * @param fixedResult   修正结果
     * @param isProvider    是否为生产者
     */
    void onBefore(String sourceName, RequestEntity requestEntity, FlowControlResult fixedResult, boolean isProvider);

    /**
     * 后置方法
     *
     * @param sourceName 发起原, 建议为目标拦截器权限定名, 该值用于在跨多个拦截器时区分线程变量
     * @param result       响应结果
     * @param isProvider   是否为生产者
     * @param hasException 是否发生调用异常， dubbo场景发生异常会调用after方法
     */
    void onAfter(String sourceName, Object result, boolean isProvider, boolean hasException);

    /**
     * 异常抛出方法
     *
     * @param sourceName 发起原, 建议为目标拦截器权限定名, 该值用于在跨多个拦截器时区分线程变量
     * @param throwable  异常信息
     * @param isProvider 是否为生产者
     * @return 是否需要重试
     */
    boolean onThrow(String sourceName, Throwable throwable, boolean isProvider);
}
