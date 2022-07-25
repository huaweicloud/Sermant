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

package com.huaweicloud.sermant.router.dubbo.utils;

import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.label.entity.Match;
import com.huaweicloud.sermant.router.config.label.entity.MatchRule;
import com.huaweicloud.sermant.router.config.label.entity.MatchStrategy;
import com.huaweicloud.sermant.router.config.label.entity.Route;
import com.huaweicloud.sermant.router.config.label.entity.Rule;
import com.huaweicloud.sermant.router.config.label.entity.ValueMatch;
import com.huaweicloud.sermant.router.dubbo.strategy.TypeStrategyChooser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 路由插件工具类
 *
 * @author provenceee
 * @since 2021-06-21
 */
public class RouteUtils {
    private RouteUtils() {
    }

    /**
     * 获取匹配的路由
     *
     * @param list 有效的规则
     * @param arguments dubbo的参数
     * @return 匹配的路由
     */
    public static List<Route> getRoutes(List<Rule> list, Object[] arguments) {
        for (Rule rule : list) {
            List<Route> routeList = getRoutes(arguments, rule);
            if (!CollectionUtils.isEmpty(routeList)) {
                return routeList;
            }
        }
        return Collections.emptyList();
    }

    private static List<Route> getRoutes(Object[] arguments, Rule rule) {
        Match match = rule.getMatch();
        boolean isFullMatch = match.isFullMatch();
        Map<String, List<MatchRule>> args = match.getArgs();
        for (Entry<String, List<MatchRule>> entry : args.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(RouterConstant.DUBBO_SOURCE_TYPE_PREFIX)) {
                continue;
            }
            List<MatchRule> matchRuleList = entry.getValue();
            for (MatchRule matchRule : matchRuleList) {
                ValueMatch valueMatch = matchRule.getValueMatch();
                List<String> values = valueMatch.getValues();
                MatchStrategy matchStrategy = valueMatch.getMatchStrategy();
                String arg = TypeStrategyChooser.INSTANCE.getValue(matchRule.getType(), key, arguments).orElse(null);
                if (!isFullMatch && matchStrategy.isMatch(values, arg, matchRule.isCaseInsensitive())) {
                    // 如果不是全匹配，且匹配了一个，那么直接return
                    return rule.getRoute();
                }
                if (isFullMatch && !matchStrategy.isMatch(values, arg, matchRule.isCaseInsensitive())) {
                    // 如果是全匹配，且有一个不匹配，则继续下一个规则
                    return Collections.emptyList();
                }
            }
        }
        if (isFullMatch) {
            // 如果是全匹配，走到这里，说明没有不匹配的，直接return
            return rule.getRoute();
        }

        // 如果不是全匹配，走到这里，说明没有一个规则能够匹配上，则继续下一个规则
        return Collections.emptyList();
    }
}