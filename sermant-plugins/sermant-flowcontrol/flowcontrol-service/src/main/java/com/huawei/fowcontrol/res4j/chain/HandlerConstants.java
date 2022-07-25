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

/**
 * 处理器常量
 *
 * @author zhouss
 * @since 2022-07-05
 */
public class HandlerConstants {
    /**
     * 限流优先级
     */
    public static final int RATE_LIMIT_ORDER = 0;

    /**
     * 隔离仓优先级
     */
    public static final int BULK_HEAD_ORDER = 1000;

    /**
     * 熔断优先级
     */
    public static final int CIRCUIT_BREAKER_ORDER = 10000;

    /**
     * 实例隔离优先级
     */
    public static final int INSTANCE_ISOLATION_ORDER = 11000;

    /**
     * 标记当前线程是否发生异常
     */
    public static final String OCCURRED_EXCEPTION = "__OCCURRED_EXCEPTION__";

    /**
     * 线程变量provider端key前缀
     */
    public static final String THREAD_LOCAL_DUBBO_PROVIDER_PREFIX = "PROVIDER:";

    /**
     * 线程变量consumer端key前缀
     */
    public static final String THREAD_LOCAL_DUBBO_CONSUMER_PREFIX = "CONSUMER:";

    /**
     * 键前缀
     */
    public static final String THREAD_LOCAL_KEY_PREFIX = HandlerConstants.class.getName()
            + "___THREAD_LOCAL_KEY_PREFIX___";

    /**
     * 空串
     */
    public static final String EMPTY_STR = "";

    private HandlerConstants() {
    }
}
