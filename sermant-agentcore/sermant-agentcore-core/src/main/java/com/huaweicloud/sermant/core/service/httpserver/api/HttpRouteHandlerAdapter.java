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
 * Http路由处理器适配器抽象类
 *
 * @author zwmagic
 * @since 2024-02-03
 */
public abstract class HttpRouteHandlerAdapter implements HttpRouteHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        try {
            ResponseResult<?> result = doHandle(request);
            response.writeBodyAsJson(result);
        } catch (Exception e) {
            response.writeBodyAsJson(ResponseResult.ofFailure(e));
        }
    }

    /**
     * 执行处理请求的方法。
     *
     * @param <T> 泛型类型
     * @param request HTTP请求对象
     * @return 处理请求后的结果对象
     */
    public abstract <T> ResponseResult<T> doHandle(HttpRequest request);
}

