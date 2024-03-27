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

package com.huaweicloud.sermant.core.service.httpserver.exception;

/**
 * HTTP server 异常
 *
 * @author zwmagic
 * @since 2024-02-01
 */
public class HttpServerException extends RuntimeException {

    /**
     * HttpServerException类的私有成员变量，表示HTTP状态码。
     */
    private final int status;

    /**
     * 构造函数，用于创建一个HttpServerException对象。
     *
     * @param status HTTP状态码
     * @param message 异常信息
     */
    public HttpServerException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * 构造函数，用于创建一个HttpServerException对象。
     *
     * @param status HTTP状态码
     * @param message 异常信息
     * @param cause 异常的原始异常
     */
    public HttpServerException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * 构造函数，用于创建一个HttpServerException对象。
     *
     * @param status HTTP状态码
     * @param cause 异常的原始异常
     */
    public HttpServerException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * 获取HTTP状态码。
     *
     * @return HTTP状态码
     */
    public int getStatus() {
        return status;
    }

}
