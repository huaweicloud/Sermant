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

package com.huaweicloud.sermant.router.spring.cache;

import java.util.List;
import java.util.Map;

/**
 * 请求数据
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class RequestData extends RequestHeader {
    private final String path;

    private final String httpMethod;

    /**
     * 构造方法
     *
     * @param header 请求头
     * @param path 请求路径
     * @param httpMethod 请求方法
     */
    public RequestData(Map<String, List<String>> header, String path, String httpMethod) {
        super(header);
        this.path = path;
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}