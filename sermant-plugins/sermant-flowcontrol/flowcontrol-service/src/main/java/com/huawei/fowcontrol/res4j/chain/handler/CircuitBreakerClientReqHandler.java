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

package com.huawei.fowcontrol.res4j.chain.handler;

import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;

/**
 * 熔断服务端处理器
 *
 * @author zhouss
 * @since 2022-07-23
 */
public class CircuitBreakerClientReqHandler extends CircuitBreakerRequestHandler {
    private static final String CONTEXT_NAME = CircuitBreakerClientReqHandler.class.getName();

    private static final String START_TIME = CONTEXT_NAME + "_START_TIME";

    @Override
    protected String getContextName() {
        return START_TIME;
    }

    @Override
    protected String getStartTime() {
        return CONTEXT_NAME;
    }

    @Override
    protected RequestType direct() {
        return RequestType.CLIENT;
    }
}
