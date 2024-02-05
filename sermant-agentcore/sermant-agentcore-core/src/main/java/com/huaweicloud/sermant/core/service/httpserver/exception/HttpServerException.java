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

    private final int status;

    public HttpServerException(int status, String message) {
        super(message);
        this.status = status;
    }

    public HttpServerException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpServerException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
