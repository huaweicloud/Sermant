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

package com.huaweicloud.sermant.router.spring.declarer;

import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * Ribbon BaseLoadBalancer负载均衡增强类，筛选下游实例
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class BaseLoadBalancerDeclarer extends AbstractDeclarer {
    private static final String ENHANCE_CLASS = "com.netflix.loadbalancer.BaseLoadBalancer";

    private static final String INTERCEPT_CLASS
        = "com.huaweicloud.sermant.router.spring.interceptor.BaseLoadBalancerInterceptor";

    private static final String[] METHOD_NAME = {"getReachableServers", "getAllServers"};

    /**
     * 构造方法
     */
    public BaseLoadBalancerDeclarer() {
        super(ENHANCE_CLASS, INTERCEPT_CLASS, null);
    }

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    /**
     * 获取方法匹配器
     *
     * @return 方法匹配器
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.nameContains(METHOD_NAME);
    }
}