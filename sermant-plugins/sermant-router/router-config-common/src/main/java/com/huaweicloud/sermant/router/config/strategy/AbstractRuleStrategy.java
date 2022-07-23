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

package com.huaweicloud.sermant.router.config.strategy;

import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.label.entity.Route;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;
import com.huaweicloud.sermant.router.config.utils.RuleUtils.RouteResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 匹配策略
 *
 * @param <T> 泛型
 * @author provenceee
 * @since 2021-10-14
 */
public abstract class AbstractRuleStrategy<T> implements RuleStrategy<T> {
    private final InstanceStrategy<T> mismatchInstanceStrategy;

    private final InstanceStrategy<T> targetInstanceStrategy;

    private final Function<T, Map<String, String>> mapper;

    /**
     * 构造方法
     *
     * @param targetInstanceStrategy 目标策略
     * @param mismatchInstanceStrategy 匹配不上的策略
     * @param mapper 获取metadata的方法
     */
    public AbstractRuleStrategy(InstanceStrategy<T> targetInstanceStrategy,
        InstanceStrategy<T> mismatchInstanceStrategy, Function<T, Map<String, String>> mapper) {
        this.targetInstanceStrategy = targetInstanceStrategy;
        this.mismatchInstanceStrategy = mismatchInstanceStrategy;
        this.mapper = mapper;
    }

    @Override
    public List<T> getTargetInstances(List<Route> routes, List<T> instances) {
        RouteResult result = RuleUtils.getTargetTags(routes);
        InstanceStrategy<T> instanceStrategy = getStrategy(result.isMatch());
        List<T> resultList = new ArrayList<>();
        for (T instance : instances) {
            if (instanceStrategy.isMatch(instance, result.getTags(), getMapper())) {
                resultList.add(instance);
            }
        }
        return CollectionUtils.isEmpty(resultList) ? instances : resultList;
    }

    @Override
    public List<T> getMismatchInstances(List<Map<String, String>> tags, List<T> instances) {
        List<T> resultList = new ArrayList<>();
        for (T instance : instances) {
            if (mismatchInstanceStrategy.isMatch(instance, tags, getMapper())) {
                resultList.add(instance);
            }
        }
        return CollectionUtils.isEmpty(resultList) ? instances : resultList;
    }

    /**
     * 获取mapper
     *
     * @return mapper
     */
    public Function<T, Map<String, String>> getMapper() {
        return mapper;
    }

    /**
     * 获取策略名
     *
     * @return 策略名
     */
    public List<String> getName() {
        return Collections.emptyList();
    }

    private InstanceStrategy<T> getStrategy(boolean isMatch) {
        return isMatch ? targetInstanceStrategy : mismatchInstanceStrategy;
    }
}