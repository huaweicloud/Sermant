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

package com.huaweicloud.sermant.router.spring.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.router.spring.cache.RequestData;
import com.huaweicloud.sermant.router.spring.cache.RequestHeader;
import com.huaweicloud.sermant.router.spring.utils.ThreadLocalUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ClientHttpRequestInterceptor增强类，发起restTemplate请求方法
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class ClientHttpRequestInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object argument = context.getArguments()[0];
        if (argument instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) argument;
            HttpHeaders headers = request.getHeaders();
            putIfAbsent(headers);
            String path = request.getURI().getPath();
            ThreadLocalUtils.setRequestData(new RequestData(getHeader(headers), path, request.getMethod().name()));
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        ThreadLocalUtils.removeRequestData();
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) {
        ThreadLocalUtils.removeRequestData();
        return context;
    }

    private void putIfAbsent(HttpHeaders headers) {
        RequestHeader requestHeader = ThreadLocalUtils.getRequestHeader();
        if (requestHeader != null) {
            Map<String, List<String>> header = requestHeader.getHeader();
            for (Entry<String, List<String>> entry : header.entrySet()) {
                // 使用上游传递的header
                headers.putIfAbsent(entry.getKey(), new LinkedList<>(entry.getValue()));
            }
        }
    }

    private Map<String, List<String>> getHeader(HttpHeaders headers) {
        Map<String, List<String>> map = new HashMap<>();
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}