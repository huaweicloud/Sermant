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

package com.huawei.flowcontrol.retry;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * 用于重试拦截获取调用实例, 当前拦截器仅对SpringCloud loadbalancer生效
 *
 * @author zhouss
 * @since 2022-05-17
 */
public class SpringLbDeclarer extends AbstractPluginDeclarer {
    /**
     * 负载均衡增强
     */
    private static final String[] ENHANCE_CLASSES = {
        "org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer",
        "org.springframework.cloud.loadbalancer.core.RandomLoadBalancer"
    };

    /**
     * 拦截类的全限定名
     */
    private static final String INTERCEPT_CLASS = SpringLbChooseServerInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASSES);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("getInstanceResponse")
                        .and(MethodMatcher.paramTypesEqual("java.util.List")), INTERCEPT_CLASS)
        };
    }
}
