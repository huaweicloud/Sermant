/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.core.service.httpserver.api;

/**
 * HTTP请求方法枚举类
 *
 * @author zwmagic
 * @since 2024-02-03
 */
public enum HttpMethod {

    /**
     * 所有请求方法
     */
    ALL,

    /**
     * GET请求方法
     */
    GET,

    /**
     * POST请求方法
     */
    POST,

    /**
     * PUT请求方法
     */
    PUT,

    /**
     * DELETE请求方法
     */
    DELETE,

    /**
     * PATCH请求方法
     */
    PATCH,

    /**
     * HEAD请求方法
     */
    HEAD,

    /**
     * OPTIONS请求方法
     */
    OPTIONS;

}

