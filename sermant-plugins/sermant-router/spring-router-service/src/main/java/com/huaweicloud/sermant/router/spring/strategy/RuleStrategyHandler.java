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

package com.huaweicloud.sermant.router.spring.strategy;

import com.huaweicloud.sermant.router.config.label.entity.Route;
import com.huaweicloud.sermant.router.config.strategy.AbstractRuleStrategy;
import com.huaweicloud.sermant.router.config.strategy.RuleStrategy;
import com.huaweicloud.sermant.router.spring.strategy.rule.DefaultRuleStrategy;
import com.huaweicloud.sermant.router.spring.strategy.rule.EurekaRuleStrategy;
import com.huaweicloud.sermant.router.spring.strategy.rule.ZookeeperRuleStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由策略处理器
 *
 * @author provenceee
 * @since 2021-10-14
 */
public enum RuleStrategyHandler {
    /**
     * 实例
     */
    INSTANCE();

    private final Map<String, RuleStrategy<Object>> map;

    private final RuleStrategy<Object> defaultStrategy;

    RuleStrategyHandler() {
        map = new HashMap<>();
        defaultStrategy = new DefaultRuleStrategy();
        init(new EurekaRuleStrategy());
        init(new ZookeeperRuleStrategy());
    }

    private void init(AbstractRuleStrategy<Object> ruleStrategy) {
        for (String name : ruleStrategy.getName()) {
            map.put(name, ruleStrategy);
        }
    }

    /**
     * 选取路由匹配的实例
     *
     * @param routes 路由规则
     * @param instances 实例列表
     * @return 路由匹配的实例
     */
    public List<Object> getTargetInstances(List<Route> routes, List<Object> instances) {
        return choose(instances.get(0)).getTargetInstances(routes, instances);
    }

    /**
     * 选取不匹配标签的实例
     *
     * @param tags 标签
     * @param instances 实例列表
     * @return 路由过滤后的实例
     */
    public List<Object> getMismatchInstances(List<Map<String, String>> tags, List<Object> instances) {
        return choose(instances.get(0)).getMismatchInstances(tags, instances);
    }

    private RuleStrategy<Object> choose(Object obj) {
        return map.getOrDefault(obj.getClass().getCanonicalName(), defaultStrategy);
    }
}