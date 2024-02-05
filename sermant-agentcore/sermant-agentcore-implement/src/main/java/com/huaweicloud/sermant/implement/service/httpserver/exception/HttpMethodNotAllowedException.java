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

package com.huaweicloud.sermant.implement.service.httpserver.exception;

import com.huaweicloud.sermant.core.service.httpserver.exception.HttpServerException;
import com.huaweicloud.sermant.implement.service.httpserver.common.Constants;

/**
 * 表示请求方法不允许的异常情况
 *
 * @author zwmagic
 * @since 2024-02-03
 */
public class HttpMethodNotAllowedException extends HttpServerException {

    public HttpMethodNotAllowedException(String message) {
        super(Constants.METHOD_NOT_ALLOWED_STATUS, message);
    }

}