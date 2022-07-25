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

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.router.common.utils.ReflectUtils;
import com.huaweicloud.sermant.router.spring.cache.RequestData;
import com.huaweicloud.sermant.router.spring.cache.RequestHeader;
import com.huaweicloud.sermant.router.spring.utils.ThreadLocalUtils;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

import feign.Request;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client增强类，发起feign请求方法
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class FeignClientInterceptor extends AbstractInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final String MODIFIERS_FIELD_NAME = "modifiers";

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object argument = context.getArguments()[0];
        if (argument instanceof Request) {
            Request request = (Request) argument;
            String path = URI.create(request.url()).getPath();
            Map<String, List<String>> headers = getHeaders(request.headers());
            setHeaders(request, headers);
            ThreadLocalUtils.setRequestData(new RequestData(headers, path, request.method()));
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

    private Map<String, List<String>> getHeaders(Map<String, Collection<String>> headers) {
        // 传入的headers是一个unmodifiableMap，所以这里要new一个
        Map<String, List<String>> newHeaders = new HashMap<>();
        if (headers != null) {
            headers.forEach((key, value) -> newHeaders.put(key, new ArrayList<>(value)));
        }
        getRequestHeader().ifPresent(requestHeader -> {
            Map<String, List<String>> header = requestHeader.getHeader();
            for (Entry<String, List<String>> entry : header.entrySet()) {
                // 使用上游传递的header
                newHeaders.putIfAbsent(entry.getKey(), entry.getValue());
            }
        });
        return Collections.unmodifiableMap(newHeaders);
    }

    private void setHeaders(Request request, Map<String, List<String>> headers) {
        try {
            Field field = request.getClass().getDeclaredField("headers");

            // 去掉final
            Field modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD_NAME);
            ReflectUtils.getAccessibleObject(modifiersField).setInt(field, field.getModifiers() & ~Modifier.FINAL);
            ReflectUtils.getAccessibleObject(field).set(request, headers);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.log(Level.WARNING, "Fail to set the headers.", e);
        }
    }

    private Optional<RequestHeader> getRequestHeader() {
        RequestHeader header = ThreadLocalUtils.getRequestHeader();
        if (header != null) {
            return Optional.of(header);
        }
        HystrixRequestContext context = HystrixRequestContext.getContextForCurrentThread();
        if (context == null) {
            return Optional.empty();
        }
        Map<HystrixRequestVariableDefault<?>, ?> state = ReflectUtils.getFieldValue(context, "state")
            .map(value -> (Map<HystrixRequestVariableDefault<?>, ?>) value).orElse(Collections.emptyMap());
        for (Entry<HystrixRequestVariableDefault<?>, ?> entry : state.entrySet()) {
            Object lazyInitializer = entry.getValue();
            Object obj = ReflectUtils.getFieldValue(lazyInitializer, "value").orElse(null);
            if (obj instanceof RequestHeader) {
                entry.getKey().remove();
                return Optional.of((RequestHeader) obj);
            }
        }
        return Optional.empty();
    }
}