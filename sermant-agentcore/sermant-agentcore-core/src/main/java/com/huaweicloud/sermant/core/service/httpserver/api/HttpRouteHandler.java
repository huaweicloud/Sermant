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
package com.huaweicloud.sermant.core.service.httpserver.api;

/**
 * Http路由处理器接口
 * @author zwmagic
 * @since 2024-02-02
 */
public interface HttpRouteHandler {

    /**
     * 处理HTTP请求
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws Exception 如果处理过程中发生异常
     */
    void handle(HttpRequest request, HttpResponse response) throws Exception;

}

