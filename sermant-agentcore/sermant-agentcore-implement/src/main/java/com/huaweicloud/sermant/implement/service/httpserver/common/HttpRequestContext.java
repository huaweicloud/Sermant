/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR C¬ONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huaweicloud.sermant.implement.service.httpserver.common;

import com.huaweicloud.sermant.core.service.httpserver.api.HttpRequest;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpResponse;

/**
 * @author zwmagic
 * @since 2024-02-02
 */
public class HttpRequestContext {
    private final String pluginId;

    private final HttpRequest request;

    private final HttpResponse response;

    public HttpRequestContext(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        String[] array = getPath().split("/");
        if (array.length < 2) {
            throw new RuntimeException("请求的 path[" + getPath() + "] 格式不对");
        }
        this.pluginId = array[1];
    }

    public String getPath() {
        return request.path();
    }

    public String getPluginId() {
        return pluginId;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
