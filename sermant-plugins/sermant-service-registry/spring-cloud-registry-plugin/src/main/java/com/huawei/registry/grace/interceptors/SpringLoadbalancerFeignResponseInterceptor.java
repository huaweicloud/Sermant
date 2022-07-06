/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on org/apache/dubbo/rpc/protocol/dubbo/DubboInvoker.java
 * from the Apache Dubbo project.
 */

package com.huawei.registry.grace.interceptors;

import com.huawei.registry.config.grace.GraceConstants;
import com.huawei.registry.config.grace.GraceContext;
import com.huawei.registry.config.grace.GraceHelper;
import com.huawei.registry.utils.RefreshUtils;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;

import feign.Request;
import feign.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

/**
 * 注入请求拦截器
 *
 * @author zhouss
 * @since 2022-05-23
 */
public class SpringLoadbalancerFeignResponseInterceptor extends GraceSwitchInterceptor {
    @Override
    protected ExecuteContext doBefore(ExecuteContext context) {
        Object[] arguments = context.getArguments();
        Request request = (Request) arguments[0];
        HashMap<String, Collection<String>> headers = new HashMap<>(request.headers());
        headers.putAll(getGraceIpHeaders());
        arguments[0] = Request.create(request.method(), request.url(), headers, request.body(), request.charset());
        return context;
    }

    @Override
    protected ExecuteContext doAfter(ExecuteContext context) {
        final Object result = context.getResult();
        final Object requestArgument = context.getArguments()[0];
        if (!(result instanceof Response) || !(requestArgument instanceof Request)) {
            return context;
        }
        Response response = (Response) result;
        if (response.headers() == null || response.headers().size() == 0) {
            return context;
        }
        final Collection<String> endpoints = response.headers().get(GraceConstants.MARK_SHUTDOWN_SERVICE_ENDPOINT);
        if (endpoints == null || endpoints.isEmpty()) {
            return context;
        }
        final String shutdownEndpoint = endpoints.iterator().next();
        GraceContext.INSTANCE.getGraceShutDownManager().addShutdownEndpoint(shutdownEndpoint);
        Request request = (Request) requestArgument;
        final Optional<String> serviceNameFromReqUrl = GraceHelper.getServiceNameFromReqUrl(request.url());
        RefreshUtils.refreshTargetServiceInstances(serviceNameFromReqUrl.orElse(null),
                response.headers().get(GraceConstants.MARK_SHUTDOWN_SERVICE_NAME));
        return context;
    }

    @Override
    protected boolean isEnabled() {
        return super.isEnabled() && graceConfig.isEnableGraceShutdown();
    }
}
