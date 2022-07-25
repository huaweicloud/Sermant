/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol;

import com.huawei.flowcontrol.common.adapte.cse.entity.FlowControlServiceMeta;
import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.entity.HttpRequestEntity;
import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;
import com.huawei.flowcontrol.service.InterceptorSupporter;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DispatcherServlet 的 API接口增强 埋点定义sentinel资源
 *
 * @author zhouss
 * @since 2022-02-11
 */
public class DispatcherServletInterceptor extends InterceptorSupporter {
    private final String className = DispatcherServletInterceptor.class.getName();

    /**
     * http请求数据转换 适应plugin -> service数据传递 注意，该方法不可抽出，由于宿主依赖仅可由该拦截器加载，因此抽出会导致找不到类
     *
     * @param request 请求
     * @return HttpRequestEntity
     */
    private Optional<HttpRequestEntity> convertToHttpEntity(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        return Optional.of(new HttpRequestEntity.Builder()
                .setRequestType(RequestType.SERVER)
                .setPathInfo(request.getPathInfo())
                .setServletPath(request.getRequestURI())
                .setHeaders(getHeaders(request))
                .setMethod(request.getMethod())
                .setServiceName(FlowControlServiceMeta.getInstance().getServiceName())
                .build());
    }

    /**
     * 获取http请求头信息
     *
     * @param request 请求信息
     * @return headers
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        final Enumeration<String> headerNames = request.getHeaderNames();
        final Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return Collections.unmodifiableMap(headers);
    }

    @Override
    protected final ExecuteContext doBefore(ExecuteContext context) throws Exception {
        final Object[] allArguments = context.getArguments();
        final HttpServletRequest argument = (HttpServletRequest) allArguments[0];
        final FlowControlResult result = new FlowControlResult();
        final Optional<HttpRequestEntity> httpRequestEntity = convertToHttpEntity(argument);
        if (!httpRequestEntity.isPresent()) {
            return context;
        }
        chooseHttpService().onBefore(className, httpRequestEntity.get(), result);
        if (result.isSkip()) {
            context.skip(null);
            final HttpServletResponse response = (HttpServletResponse) allArguments[1];
            if (response != null) {
                response.setStatus(result.getResult().getCode());
                response.getWriter().print(result.buildResponseMsg());
            }
        }
        return context;
    }

    @Override
    protected final ExecuteContext doAfter(ExecuteContext context) {
        chooseHttpService().onAfter(className, context.getResult());
        return context;
    }

    @Override
    protected final ExecuteContext doThrow(ExecuteContext context) {
        chooseHttpService().onThrow(className, context.getThrowable());
        return context;
    }
}
