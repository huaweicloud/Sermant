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

package com.huawei.fowcontrol.res4j.service;

import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.entity.RequestEntity;
import com.huawei.flowcontrol.service.rest4j.DubboRest4jService;
import com.huawei.fowcontrol.res4j.chain.HandlerChainEntry;

/**
 * dubbo请求拦截
 *
 * @author zhouss
 * @since 2022-01-25
 */
public class DubboRest4jServiceImpl extends DubboRest4jService {
    private final Exception dubboException = new Exception("dubbo exception");

    @Override
    public void onBefore(String sourceName, RequestEntity requestEntity, FlowControlResult flowControlResult,
            boolean isProvider) {
        HandlerChainEntry.INSTANCE.onDubboBefore(sourceName, requestEntity, flowControlResult, isProvider);
    }

    @Override
    public void onAfter(String sourceName, Object result, boolean isProvider, boolean hasException) {
        if (hasException) {
            // 此处主要标记异常状态，具体异常信息与记录结果无关
            HandlerChainEntry.INSTANCE.onDubboThrow(sourceName, dubboException, isProvider);
        }
        HandlerChainEntry.INSTANCE.onDubboResult(sourceName, result, isProvider);
    }

    @Override
    public boolean onThrow(String sourceName, Throwable throwable, boolean isProvider) {
        HandlerChainEntry.INSTANCE.onDubboThrow(sourceName, dubboException, isProvider);
        return false;
    }
}
