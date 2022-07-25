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

package com.huaweicloud.sermant.router.dubbo.strategy;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.dubbo.strategy.type.ArrayTypeStrategy;
import com.huaweicloud.sermant.router.dubbo.strategy.type.EmptyTypeStrategy;
import com.huaweicloud.sermant.router.dubbo.strategy.type.EnabledTypeStrategy;
import com.huaweicloud.sermant.router.dubbo.strategy.type.ListTypeStrategy;
import com.huaweicloud.sermant.router.dubbo.strategy.type.MapTypeStrategy;
import com.huaweicloud.sermant.router.dubbo.strategy.type.ObjectTypeStrategy;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 规则策略选择器
 *
 * @author provenceee
 * @since 2021-10-13
 */
public enum TypeStrategyChooser {
    /**
     * 单例
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final Set<TypeStrategy> typeStrategies;

    TypeStrategyChooser() {
        typeStrategies = new HashSet<>();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        registerTypeStrategy(new ArrayTypeStrategy());
        registerTypeStrategy(new EnabledTypeStrategy());
        registerTypeStrategy(new ListTypeStrategy());
        registerTypeStrategy(new MapTypeStrategy());
        registerTypeStrategy(new EmptyTypeStrategy());
        registerTypeStrategy(new ObjectTypeStrategy());
    }

    /**
     * 策略注册
     *
     * @param typeStrategy 表达式策略
     */
    private void registerTypeStrategy(TypeStrategy typeStrategy) {
        typeStrategies.add(typeStrategy);
    }

    /**
     * 选择策略
     *
     * @param type 标签规则策略表达式
     * @return 规则策略
     */
    private Optional<TypeStrategy> choose(String type) {
        for (TypeStrategy typeStrategy : typeStrategies) {
            if (typeStrategy.isMatch(type)) {
                return Optional.of(typeStrategy);
            }
        }
        LOGGER.warning("Cannot found the type strategy, type is " + type);
        return Optional.empty();
    }

    /**
     * 根据策略表达式获取参数值
     *
     * @param type 策略表达式
     * @param key 参数索引
     * @param arguments 参数数组
     * @return 参数值
     */
    public Optional<String> getValue(String type, String key, Object[] arguments) {
        if (arguments == null) {
            return Optional.empty();
        }
        Optional<TypeStrategy> typeStrategy = choose(type);
        if (!typeStrategy.isPresent()) {
            return Optional.empty();
        }
        int index;
        try {
            index = Integer.parseInt(key.substring(RouterConstant.DUBBO_SOURCE_TYPE_PREFIX.length()));
        } catch (NumberFormatException e) {
            LOGGER.warning("Source type " + key + " is invalid.");
            return Optional.empty();
        }
        if (index < 0 || index >= arguments.length || arguments[index] == null) {
            return Optional.empty();
        }
        return typeStrategy.get().getValue(arguments[index], type);
    }
}