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

package com.huaweicloud.sermant.router.dubbo.declarer;

import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * 增强SpringBootApplication类的main方法
 *
 * @author provenceee
 * @since 2022-01-24
 */
public class SpringBootDeclarer extends AbstractDeclarer {
    private static final String[] ENHANCE_CLASS = {"org.springframework.boot.autoconfigure.SpringBootApplication"};

    private static final String INTERCEPT_CLASS
        = "com.huaweicloud.sermant.router.dubbo.interceptor.SpringBootInterceptor";

    private static final String METHOD_NAME = "main";

    /**
     * 构造方法
     */
    public SpringBootDeclarer() {
        super(ENHANCE_CLASS, INTERCEPT_CLASS, METHOD_NAME);
    }

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.isAnnotatedWith(ENHANCE_CLASS);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return super.getMethodMatcher().and(MethodMatcher.isStaticMethod());
    }
}
