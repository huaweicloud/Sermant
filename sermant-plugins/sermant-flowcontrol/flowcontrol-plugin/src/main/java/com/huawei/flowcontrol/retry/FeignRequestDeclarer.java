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

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * spring http请求拦截
 *
 * @author zhouss
 * @since 2022-01-27
 */
public class FeignRequestDeclarer extends AbstractPluginDeclarer {
    /**
     * 增强类的全限定名
     */
    private static final String ENHANCE_CLASS = "org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient";

    private static final String SPRING_LOADBALANCER_ENHANCE_CLASS =
            "org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient";

    private static final String BLOCKING_ENHANCE_CLASS =
        "org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient";

    private static final String RETRY_BLOCKING_ENHANCE_CLASS =
        "org.springframework.cloud.openfeign.loadbalancer.RetryableFeignBlockingLoadBalancerClient";

    /**
     * 拦截类的全限定名
     */
    private static final String INTERCEPT_CLASS = FeignRequestInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASS, BLOCKING_ENHANCE_CLASS, RETRY_BLOCKING_ENHANCE_CLASS,
                SPRING_LOADBALANCER_ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(MethodMatcher.nameEquals("execute"), INTERCEPT_CLASS)
        };
    }
}
