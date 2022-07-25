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

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * 实例增强基类
 *
 * @author provenceee
 * @since 2022-01-18
 */
public abstract class AbstractDeclarer extends AbstractPluginDeclarer {
    private final String enhanceClass;

    private final String interceptClass;

    private final String methodName;

    /**
     * 构造方法
     *
     * @param enhanceClass 增强类
     * @param interceptClass 拦截类
     * @param methodName 增强方法
     */
    public AbstractDeclarer(String enhanceClass, String interceptClass, String methodName) {
        this.enhanceClass = enhanceClass;
        this.interceptClass = interceptClass;
        this.methodName = methodName;
    }

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.isExtendedFrom(enhanceClass);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(getMethodMatcher(), interceptClass)
        };
    }

    /**
     * 获取方法匹配器
     *
     * @return 方法匹配器
     */
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.nameEquals(methodName);
    }
}