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

package com.huawei.flowcontrol.common.enums;

import com.huawei.flowcontrol.common.config.CommonConst;

/**
 * 流控拦截提示
 *
 * @author zhouss
 * @since 2022-01-24
 */
public enum FlowControlEnum {
    /**
     * 流控异常
     */
    RATE_LIMITED("Flow Limited", CommonConst.TOO_MANY_REQUEST_CODE),

    /**
     * 熔断异常
     */
    CIRCUIT_BREAKER("Degraded and blocked", CommonConst.TOO_MANY_REQUEST_CODE),

    /**
     * 实例隔离异常
     */
    INSTANCE_ISOLATION("The instance that it invoked has been isolated", CommonConst.INSTANCE_ISOLATION_REQUEST_CODE),

    /**
     * 隔离仓异常
     */
    BULKHEAD_FULL("Exceeded the max concurrent calls!", CommonConst.TOO_MANY_REQUEST_CODE);

    /**
     * 提示信息
     */
    private final String msg;

    /**
     * 响应码
     */
    private final int code;

    FlowControlEnum(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
