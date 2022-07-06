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

package com.huawei.flowcontrol.common.handler;

import com.huawei.flowcontrol.common.adapte.cse.ResolverManager;
import com.huawei.flowcontrol.common.adapte.cse.match.MatchManager;
import com.huawei.flowcontrol.common.adapte.cse.resolver.AbstractResolver;
import com.huawei.flowcontrol.common.adapte.cse.rule.AbstractRule;
import com.huawei.flowcontrol.common.entity.RequestEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 拦截请求处理器
 *
 * @param <H> 处理器 对应 resilience4j个处理器
 * @param <R> resolver解析规则
 * @author zhouss
 * @since 2022-01-22
 */
public abstract class AbstractRequestHandler<H, R extends AbstractRule> {
    /**
     * 处理器缓存 map 业务场景名, 处理器
     */
    private final Map<String, Optional<H>> handlers = new ConcurrentHashMap<>();

    /**
     * 处理器构造方法
     */
    protected AbstractRequestHandler() {
        registerConfigListener();
    }

    private void registerConfigListener() {
        ResolverManager.INSTANCE.registerListener(configKey(), (updateKey, rules) -> handlers.remove(updateKey));
    }

    /**
     * 获取指定请求处理器
     *
     * @param request 请求信息
     * @return handler
     */
    public List<H> getHandlers(RequestEntity request) {
        final Set<String> businessNames = MatchManager.INSTANCE.matchWithCache(request);
        if (businessNames.isEmpty()) {
            return Collections.emptyList();
        }
        return createOrGetHandlers(businessNames);
    }

    /**
     * 创建处理器
     *
     * @param businessNames 已匹配的业务名
     * @return 处理器
     */
    public List<H> createOrGetHandlers(Set<String> businessNames) {
        return businessNames.stream()
            .map(businessName -> handlers.computeIfAbsent(businessName, fn -> create(businessName)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<H> create(String businessName) {
        final AbstractResolver<?> resolver = ResolverManager.INSTANCE.getResolver(configKey());
        final R rule = (R) resolver.getRules().get(businessName);
        if (rule == null) {
            return Optional.empty();
        }
        return createProcessor(businessName, rule);
    }

    /**
     * 创建处理器
     *
     * @param businessName 业务场景名
     * @param rule         匹配的解析规则
     * @return handler
     */
    protected abstract Optional<H> createProcessor(String businessName, R rule);

    /**
     * 获取配置键
     *
     * @return 配置键， 用于注册配置监听器
     * @since 2022-03-22
     */
    protected abstract String configKey();
}
