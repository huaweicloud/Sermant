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

package com.huaweicloud.sermant.core.plugin.agent.collector;

import com.huaweicloud.sermant.core.agent.common.BeforeResult;
import com.huaweicloud.sermant.core.agent.definition.EnhanceDefinition;
import com.huaweicloud.sermant.core.agent.definition.MethodInterceptPoint;
import com.huaweicloud.sermant.core.agent.interceptor.ConstructorInterceptor;
import com.huaweicloud.sermant.core.agent.interceptor.InstanceMethodInterceptor;
import com.huaweicloud.sermant.core.agent.interceptor.StaticMethodInterceptor;
import com.huaweicloud.sermant.core.agent.matcher.NameMatcher;
import com.huaweicloud.sermant.core.agent.matcher.NonNameMatcher;
import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.PluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.Interceptor;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodType;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

/**
 * ??????????????????api??????????????????
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2022-01-25
 */
@Deprecated
public class DeprecatedPluginCollector extends AbstractPluginCollector {
    /**
     * ?????????????????????
     *
     * @param interceptor ?????????????????????
     * @param classLoader ????????????????????????????????????
     * @return ???????????????
     */
    private static Interceptor createInterceptor(String interceptor, ClassLoader classLoader) {
        try {
            return interceptorTransform((com.huaweicloud.sermant.core.agent.interceptor.Interceptor)
                    classLoader.loadClass(interceptor).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    String.format(Locale.ROOT, "Create instance of [%s] failed. ", interceptor));
        }
    }

    /**
     * ???????????????
     *
     * @param interceptor ???????????????
     * @return ???????????????
     */
    private static Interceptor
        interceptorTransform(com.huaweicloud.sermant.core.agent.interceptor.Interceptor interceptor) {
        return new Interceptor() {
            @Override
            public ExecuteContext before(ExecuteContext context) throws Exception {
                if (interceptor instanceof StaticMethodInterceptor) {
                    final BeforeResult beforeResult = new BeforeResult();
                    ((StaticMethodInterceptor)interceptor).before(context.getRawCls(), context.getMethod(),
                        context.getArguments(), beforeResult);
                    if (!beforeResult.isContinue()) {
                        context.skip(beforeResult.getResult());
                    }
                } else if (interceptor instanceof InstanceMethodInterceptor) {
                    final BeforeResult beforeResult = new BeforeResult();
                    ((InstanceMethodInterceptor)interceptor).before(context.getObject(), context.getMethod(),
                        context.getArguments(), beforeResult);
                    if (!beforeResult.isContinue()) {
                        context.skip(beforeResult.getResult());
                    }
                }
                return context;
            }

            @Override
            public ExecuteContext after(ExecuteContext context) throws Exception {
                if (interceptor instanceof StaticMethodInterceptor) {
                    context.changeResult(((StaticMethodInterceptor) interceptor).after(context.getRawCls(),
                            context.getMethod(), context.getArguments(), context.getResult()));
                } else if (interceptor instanceof InstanceMethodInterceptor) {
                    context.changeResult(((InstanceMethodInterceptor) interceptor).after(context.getObject(),
                            context.getMethod(), context.getArguments(), context.getResult()));
                } else if (interceptor instanceof ConstructorInterceptor) {
                    ((ConstructorInterceptor) interceptor).onConstruct(context.getObject(), context.getArguments());
                }
                return context;
            }

            @Override
            public ExecuteContext onThrow(ExecuteContext context) {
                if (interceptor instanceof StaticMethodInterceptor) {
                    ((StaticMethodInterceptor) interceptor).onThrow(context.getRawCls(), context.getMethod(),
                            context.getArguments(), context.getThrowable());
                } else if (interceptor instanceof InstanceMethodInterceptor) {
                    ((InstanceMethodInterceptor) interceptor).onThrow(context.getObject(), context.getMethod(),
                            context.getArguments(), context.getThrowable());
                }
                return context;
            }
        };
    }

    /**
     * ??????????????????
     *
     * @param classMatcher ??????????????????
     * @return ??????????????????
     */
    private static ClassMatcher
        classMatcherTransform(com.huaweicloud.sermant.core.agent.matcher.ClassMatcher classMatcher) {
        if (classMatcher instanceof NameMatcher) {
            return ClassMatcher.nameEquals(((NameMatcher)classMatcher).getClassName());
        } else if (classMatcher instanceof NonNameMatcher) {
            return new ClassMatcher() {
                @Override
                public boolean matches(TypeDescription target) {
                    return ((NonNameMatcher)classMatcher).isMatch(target);
                }
            };
        } else {
            return new ClassMatcher() {
                @Override
                public boolean matches(TypeDescription target) {
                    return false;
                }
            };
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param definition ????????????
     * @return ???????????????
     */
    public static PluginDeclarer enhanceDefinitionToPluginDeclarer(EnhanceDefinition definition) {
        return new AbstractPluginDeclarer() {
            @Override
            public ClassMatcher getClassMatcher() {
                return classMatcherTransform(definition.enhanceClass());
            }

            @Override
            public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
                final MethodInterceptPoint[] interceptPoints = definition.getMethodInterceptPoints();
                final InterceptDeclarer[] interceptDeclarers = new InterceptDeclarer[interceptPoints.length];
                for (int i = 0; i < interceptPoints.length; i++) {
                    final MethodInterceptPoint interceptPoint = interceptPoints[i];
                    interceptDeclarers[i] = InterceptDeclarer.build(new MethodMatcher() {
                        @Override
                        public boolean matches(MethodDescription target) {
                            final MethodType methodType;
                            if (interceptPoint.isStaticMethod()) {
                                methodType = MethodType.STATIC;
                            } else if (interceptPoint.isConstructor()) {
                                methodType = MethodType.CONSTRUCTOR;
                            } else {
                                methodType = MethodType.MEMBER;
                            }
                            return MethodMatcher.methodTypeMatches(methodType).matches(target)
                                    && interceptPoint.getMatcher().matches(target);
                        }
                    }, createInterceptor(interceptPoint.getInterceptor(), classLoader));
                }
                return interceptDeclarers;
            }
        };
    }

    @Override
    public Iterable<? extends PluginDeclarer> getDeclarers() {
        final List<PluginDeclarer> declares = new ArrayList<>();
        for (EnhanceDefinition definition : ServiceLoader.load(EnhanceDefinition.class)) {
            declares.add(DeprecatedPluginCollector.enhanceDefinitionToPluginDeclarer(definition));
        }
        return declares;
    }
}
