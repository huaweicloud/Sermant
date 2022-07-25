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
import com.huaweicloud.sermant.core.utils.ClassUtils;

import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 通过拦截增加注入拦截器，注入spring web拦截器
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class HandlerExecutionChainInterceptor extends AbstractInterceptor {
    private static final String ROUTE_HANDLER_CLASS_NAME
        = "com.huaweicloud.sermant.router.spring.interceptor.RouteHandlerInterceptor";

    private HandlerInterceptor handlerInterceptor;

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object object = context.getObject();
        if (object instanceof HandlerExecutionChain) {
            HandlerExecutionChain chain = (HandlerExecutionChain) object;
            chain.addInterceptor(getInterceptor());
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }

    private HandlerInterceptor getInterceptor() {
        if (handlerInterceptor == null) {
            synchronized (HandlerExecutionChainInterceptor.class) {
                if (handlerInterceptor == null) {
                    ClassUtils.defineClass(ROUTE_HANDLER_CLASS_NAME, getClass().getClassLoader());
                    handlerInterceptor = new RouteHandlerInterceptor();
                }
            }
        }
        return handlerInterceptor;
    }
}