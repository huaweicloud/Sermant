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
package com.huaweicloud.sermant.implement.service.httpserver;

/**
 * HttpServerProvider接口
 *
 * @author zwmagic
 * @since 2024-02-02
 */
public interface HttpServerProvider {

    /**
     * 获取类型
     *
     * @return 类型
     */
    String getType();

    /**
     * 启动HttpServer
     *
     * @throws Exception 异常
     */
    void start() throws Exception;

    /**
     * 停止HttpServer
     *
     * @throws Exception 异常
     */
    void stop() throws Exception;

}

