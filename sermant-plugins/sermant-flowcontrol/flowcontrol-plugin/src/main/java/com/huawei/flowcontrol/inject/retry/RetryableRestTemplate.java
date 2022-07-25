/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.inject.retry;

import com.huawei.flowcontrol.common.entity.HttpRequestEntity;
import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;
import com.huawei.flowcontrol.common.handler.retry.AbstractRetry;
import com.huawei.flowcontrol.common.handler.retry.RetryContext;
import com.huawei.flowcontrol.retry.handler.RetryHandlerV2;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.decorators.Decorators.DecorateCheckedSupplier;
import io.github.resilience4j.retry.Retry;
import io.vavr.CheckedFunction0;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 可重试的RestTemplate, 注意, 由于基于拦截的方式无法对网络异常等非业务抛出的异常拦截, 仅可采用注入方式实现
 *
 * @author zhouss
 * @since 2022-07-23
 */
public class RetryableRestTemplate extends RestTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final RetryHandlerV2 retryHandlerV2 = new RetryHandlerV2();

    private final com.huawei.flowcontrol.common.handler.retry.Retry retry = new HttpRetry();

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) throws RestClientException {
        ClientHttpResponse response = null;
        try {
            final ClientHttpRequest request = getRequestFactory().createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            final Optional<HttpRequestEntity> httpRequestEntity = convertToHttpEntity(request);
            if (!httpRequestEntity.isPresent()) {
                return super.doExecute(url, method, requestCallback, responseExtractor);
            } else {
                final Optional<ClientHttpResponse> clientHttpResponse = executeWithRetry(httpRequestEntity.get(), url,
                        method, requestCallback);
                if (clientHttpResponse.isPresent()) {
                    response = clientHttpResponse.get();
                    handleResponse(url, method, response);
                    return responseExtractor == null ? null : responseExtractor.extractData(response);
                }
            }
        } catch (IOException ex) {
            String resource = url.toString();
            String query = url.getRawQuery();
            resource = query != null ? resource.substring(0, resource.indexOf(query) - 1) : resource;
            throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + resource + "\": "
                    + ex.getMessage(), ex);
        } finally {
            RetryContext.INSTANCE.remove();
            if (response != null) {
                response.close();
            }
        }
        return super.doExecute(url, method, requestCallback, responseExtractor);
    }

    private Optional<ClientHttpResponse> executeWithRetry(HttpRequestEntity httpRequestEntity, URI url,
            HttpMethod method, RequestCallback requestCallback) {
        ClientHttpResponse response;
        RetryContext.INSTANCE.markRetry(retry);
        final List<Retry> handlers = retryHandlerV2.getHandlers(httpRequestEntity);
        if (handlers.isEmpty()) {
            return Optional.empty();
        }
        RetryContext.INSTANCE.buildRetryPolicy(httpRequestEntity);
        CheckedFunction0<ClientHttpResponse> next = () -> {
            ClientHttpRequest execution = createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(execution);
            }
            return execution.execute();
        };
        DecorateCheckedSupplier<ClientHttpResponse> dcs = Decorators.ofCheckedSupplier(next);
        dcs.withRetry(handlers.get(0));
        try {
            response = dcs.get();
        } catch (Throwable throwable) {
            LOGGER.log(Level.SEVERE, "Error occurred when retry", throwable);
            return Optional.of(new RetryClientHttpResponse(throwable.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
        return Optional.of(response);
    }

    /**
     * http请求数据转换 适应plugin -> service数据传递 注意，该方法不可抽出，由于宿主依赖仅可由该拦截器加载，因此抽出会导致找不到类
     *
     * @param request 请求
     * @return HttpRequestEntity
     */
    private Optional<HttpRequestEntity> convertToHttpEntity(HttpRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        return Optional.of(new HttpRequestEntity.Builder()
                .setRequestType(RequestType.CLIENT)
                .setApiPath(request.getURI().getPath())
                .setHeaders(request.getHeaders().toSingleValueMap())
                .setMethod(request.getMethod().name())
                .setServiceName(request.getURI().getHost())
                .build());
    }

    /**
     * Http请求重试
     *
     * @since 2022-02-21
     */
    public static class HttpRetry extends AbstractRetry {
        private static final String METHOD_KEY = "ClientHttpResponse#getRawStatusCode";

        @Override
        protected Optional<String> getCode(Object result) {
            final Optional<Method> getRawStatusCode = getInvokerMethod(METHOD_KEY, fn -> {
                try {
                    final Method method = result.getClass().getDeclaredMethod("getRawStatusCode");
                    method.setAccessible(true);
                    return method;
                } catch (NoSuchMethodException ex) {
                    LOGGER.warning(String.format(Locale.ENGLISH,
                            "Can not find method getRawStatusCode from response class %s",
                            result.getClass().getName()));
                }
                return placeHolderMethod;
            });
            if (!getRawStatusCode.isPresent()) {
                return Optional.empty();
            }
            try {
                return Optional.of(String.valueOf(getRawStatusCode.get().invoke(result)));
            } catch (IllegalAccessException ex) {
                LOGGER.warning(String.format(Locale.ENGLISH,
                        "Can not find method getRawStatusCode from class [%s]!",
                        result.getClass().getCanonicalName()));
            } catch (InvocationTargetException ex) {
                LOGGER.warning(String.format(Locale.ENGLISH, "Invoking method getRawStatusCode failed, reason: %s",
                        ex.getMessage()));
            }
            return Optional.empty();
        }

        @Override
        public Class<? extends Throwable>[] retryExceptions() {
            return getRetryExceptions();
        }

        @Override
        public RetryFramework retryType() {
            return RetryFramework.SPRING_CLOUD;
        }
    }
}
