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
import com.huawei.flowcontrol.service.rest4j.HttpRest4jService;
import com.huawei.fowcontrol.res4j.chain.HandlerChainEntry;

/**
 * http请求拦截逻辑实现
 *
 * @author zhouss
 * @since 2022-01-22
 */
public class HttpRest4jServiceImpl extends HttpRest4jService {
    @Override
    public void onBefore(String sourceName, RequestEntity requestEntity, FlowControlResult flowControlResult) {
        HandlerChainEntry.INSTANCE.onBefore(sourceName, requestEntity, flowControlResult);
    }

    @Override
    public void onAfter(String sourceName, Object result) {
        HandlerChainEntry.INSTANCE.onResult(sourceName, result);
    }

    @Override
    public void onThrow(String sourceName, Throwable throwable) {
        HandlerChainEntry.INSTANCE.onThrow(sourceName, throwable);
    }
}
