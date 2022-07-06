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

package com.huawei.fowcontrol.res4j.chain;

import com.huawei.fowcontrol.res4j.chain.context.RequestContext;

import java.util.Set;

/**
 * 请求处理定义
 *
 * @author zhouss
 * @since 2022-07-05
 */
public interface RequestHandler {
    /**
     * 请求处理
     *
     * @param context 请求上下文
     * @param businessNames 已匹配的业务场景名
     */
    void onBefore(RequestContext context, Set<String> businessNames);

    /**
     * 响应处理
     *
     * @param context 请求上下文
     * @param businessNames 已匹配的业务场景名
     * @param result 响应结果
     */
    void onResult(RequestContext context, Set<String> businessNames, Object result);

    /**
     * 响应处理
     *
     * @param context 请求上下文
     * @param businessNames 已匹配的业务场景名
     * @param throwable 异常
     */
    void onThrow(RequestContext context, Set<String> businessNames, Throwable throwable);

    /**
     * 优先级
     *
     * @return 优先级, 数值越小, 优先级越高
     */
    int getOrder();
}
