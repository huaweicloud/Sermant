/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.router.spring.utils;

import com.huaweicloud.sermant.router.spring.cache.RequestData;
import com.huaweicloud.sermant.router.spring.cache.RequestHeader;

/**
 * 线程变量
 *
 * @author provenceee
 * @since 2022-07-08
 */
public class ThreadLocalUtils {
    private static final ThreadLocal<RequestHeader> HEADER = new ThreadLocal<>();

    private static final ThreadLocal<RequestData> DATA = new ThreadLocal<>();

    private ThreadLocalUtils() {
    }

    /**
     * 获取线程变量
     *
     * @return 线程变量
     */
    public static RequestData getRequestData() {
        return DATA.get();
    }

    /**
     * 获取线程变量
     *
     * @return 线程变量
     */
    public static RequestHeader getRequestHeader() {
        return HEADER.get();
    }

    /**
     * 存入线程变量
     *
     * @param value 线程变量
     */
    public static void setRequestData(RequestData value) {
        DATA.set(value);
    }

    /**
     * 存入线程变量
     *
     * @param value 线程变量
     */
    public static void setRequestHeader(RequestHeader value) {
        HEADER.set(value);
    }

    /**
     * 删除线程变量
     */
    public static void removeRequestData() {
        DATA.remove();
    }

    /**
     * 删除线程变量
     */
    public static void removeRequestHeader() {
        HEADER.remove();
    }
}