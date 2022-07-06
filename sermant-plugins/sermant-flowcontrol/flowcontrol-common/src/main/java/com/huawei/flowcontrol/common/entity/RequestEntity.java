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

package com.huawei.flowcontrol.common.entity;

import com.huawei.flowcontrol.common.cache.Timed;

import java.util.Map;

/**
 * 请求信息
 *
 * @author zhouss
 * @since 2022-01-22
 */
public interface RequestEntity extends Timed {
    /**
     * 请求路径
     *
     * @return 请求路径
     */
    String getApiPath();

    /**
     * 获取请求头
     *
     * @return 请求头
     */
    Map<String, String> getHeaders();

    /**
     * 获取请求方法
     *
     * @return 方法类型
     */
    String getMethod();

    /**
     * 服务名
     *
     * @return 服务名
     */
    String getServiceName();

    /**
     * 请求方向
     *
     * @return 请求方向
     */
    RequestType getRequestType();

    /**
     * 必须实现equal方法
     *
     * @param obj 比较目标
     * @return 是否相等
     */
    @Override
    boolean equals(Object obj);

    /**
     * 必须实现hashCode编码
     *
     * @return 哈希码
     */
    @Override
    int hashCode();

    /**
     * 请求类型, 标记请求的方向, 客户端请求或者服务端请求
     *
     * @since 2022-07-20
     */
    enum RequestType {
        /**
         * 客户端请求, 即出去的请求
         */
        CLIENT,

        /**
         * 服务端请求, 即进来的请求
         */
        SERVER,

        /**
         * 均可处理
         */
        BOTH
    }
}
