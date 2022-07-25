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

import com.huaweicloud.sermant.core.service.ServiceManager;
import com.huaweicloud.sermant.router.spring.cache.RequestHeader;
import com.huaweicloud.sermant.router.spring.service.RouteHandlerService;
import com.huaweicloud.sermant.router.spring.utils.ThreadLocalUtils;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * spring拦截器
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class RouteHandlerInterceptor implements HandlerInterceptor {
    private final RouteHandlerService service;

    /**
     * 构造方法
     */
    public RouteHandlerInterceptor() {
        service = ServiceManager.getService(RouteHandlerService.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        Object obj) {
        Map<String, List<String>> header = new HashMap<>();
        Set<String> headerKeys = service.getHeaderKeys();
        List<String> headerNames = enumeration2List(httpServletRequest.getHeaderNames());
        for (String headerKey : headerKeys) {
            if (headerNames.contains(headerKey)) {
                header.put(headerKey, enumeration2List(httpServletRequest.getHeaders(headerKey)));
            }
        }
        ThreadLocalUtils.setRequestHeader(new RequestHeader(header));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object obj,
        ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        Object obj, Exception ex) {
        ThreadLocalUtils.removeRequestHeader();
    }

    private List<String> enumeration2List(Enumeration<?> enumeration) {
        if (enumeration == null) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            list.add((String) enumeration.nextElement());
        }
        return list;
    }
}