/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.retry;

import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.entity.HttpRequestEntity;
import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;
import com.huawei.flowcontrol.common.handler.retry.AbstractRetry;
import com.huawei.flowcontrol.common.handler.retry.Retry;
import com.huawei.flowcontrol.common.handler.retry.RetryContext;
import com.huawei.flowcontrol.service.InterceptorSupporter;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;

import feign.Request;
import feign.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * DispatcherServlet 的 API接口增强 埋点定义sentinel资源
 *
 * @author zhouss
 * @since 2022-02-11
 */
public class FeignRequestInterceptor extends InterceptorSupporter {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final String className = FeignRequestInterceptor.class.getName();

    private final Retry retry = new FeignRetry();

    /**
     * http请求数据转换 适应plugin -> service数据传递 注意，该方法不可抽出，由于宿主依赖仅可由该拦截器加载，因此抽出会导致找不到类
     *
     * @param request 请求
     * @return HttpRequestEntity
     */
    private Optional<HttpRequestEntity> convertToHttpEntity(Request request) {
        if (request == null) {
            return Optional.empty();
        }
        try {
            final URL url = new URL(request.url());
            final HashMap<String, String> headers = new HashMap<>(request.headers().size());
            request.headers().forEach((headerName, headValue) -> headers.put(headerName, headValue.iterator().next()));
            return Optional.of(new HttpRequestEntity.Builder()
                    .setRequestType(RequestType.CLIENT)
                    .setApiPath(url.getPath())
                    .setHeaders(headers)
                    .setMethod(request.method())
                    .setServiceName(url.getHost())
                    .build());
        } catch (MalformedURLException ignored) {
            // ignored
        }
        return Optional.of(new HttpRequestEntity());
    }

    @Override
    protected final ExecuteContext doBefore(ExecuteContext context) {
        final FlowControlResult flowControlResult = new FlowControlResult();
        final Request request = (Request) context.getArguments()[0];
        final Optional<HttpRequestEntity> httpRequestEntity = convertToHttpEntity(request);
        if (!httpRequestEntity.isPresent()) {
            return context;
        }
        chooseHttpService().onBefore(className, httpRequestEntity.get(), flowControlResult);
        if (flowControlResult.isSkip()) {
            final String responseMsg = flowControlResult.buildResponseMsg();
            context.skip(Response.builder()
                    .status(flowControlResult.getResult().getCode())
                    .body(responseMsg, StandardCharsets.UTF_8)
                    .headers(Collections.emptyMap())
                    .reason(responseMsg)
                    .request(request)
                    .build());
        } else {
            context.skip(null);
        }
        return context;
    }

    @Override
    protected ExecuteContext doThrow(ExecuteContext context) {
        chooseHttpService().onThrow(className, context.getThrowable());
        return context;
    }

    @Override
    protected final ExecuteContext doAfter(ExecuteContext context) {
        chooseHttpService().onAfter(className, context.getResult());
        final Object[] allArguments = context.getArguments();
        Request request = (Request) allArguments[0];
        Object result = context.getResult();
        try {
            final Optional<HttpRequestEntity> httpRequestEntity = convertToHttpEntity(request);
            if (!httpRequestEntity.isPresent()) {
                return context;
            }
            RetryContext.INSTANCE.markRetry(retry);
            RetryContext.INSTANCE.buildRetryPolicy(httpRequestEntity.get());
            final Supplier<Object> retryFunc = createRetryFunc(context.getObject(),
                    context.getMethod(), allArguments, context.getResult());
            result = retryFunc.get();
            final List<io.github.resilience4j.retry.Retry> handlers = getRetryHandler()
                .getHandlers(httpRequestEntity.get());
            if (!handlers.isEmpty() && needRetry(handlers.get(0), result, context.getThrowable())) {
                result = handlers.get(0).executeCheckedSupplier(retryFunc::get);
            }
        } catch (Throwable throwable) {
            LOGGER.warning(String.format(Locale.ENGLISH,
                "Failed to invoke method:%s for few times, reason:%s",
                context.getMethod().getName(), getExMsg(throwable)));
        } finally {
            RetryContext.INSTANCE.remove();
        }
        context.changeResult(result);
        return context;
    }

    /**
     * feign重试
     *
     * @since 2022-02-11
     */
    public static class FeignRetry extends AbstractRetry {
        private static final String METHOD_KEY = "Response#status";

        @Override
        public Optional<String> getCode(Object result) {
            final Optional<Method> status = getInvokerMethod(METHOD_KEY, fn -> {
                final Method method;
                try {
                    method = result.getClass().getDeclaredMethod("status");
                    method.setAccessible(true);
                    return method;
                } catch (NoSuchMethodException ex) {
                    LOGGER.warning(String.format(Locale.ENGLISH,
                        "Can not find method status from response class %s", result.getClass().getName()));
                }
                return placeHolderMethod;
            });
            if (!status.isPresent()) {
                return Optional.empty();
            }
            try {
                return Optional.of(String.valueOf(status.get().invoke(result)));
            } catch (IllegalAccessException ex) {
                LOGGER.warning(String.format(Locale.ENGLISH, "Can not find method status from class [%s]!",
                    result.getClass().getCanonicalName()));
            } catch (InvocationTargetException ex) {
                LOGGER.warning(String.format(Locale.ENGLISH, "Invoking method status failed, reason: %s",
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
