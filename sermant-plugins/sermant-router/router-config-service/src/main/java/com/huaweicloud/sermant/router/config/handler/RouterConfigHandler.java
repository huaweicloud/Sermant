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

package com.huaweicloud.sermant.router.config.handler;

import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.event.RouterEventCollector;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.entity.EntireRule;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 路由配置处理器（全集）
 *
 * @author provenceee
 * @since 2022-08-09
 */
public class RouterConfigHandler extends AbstractConfigHandler {
    @Override
    public void handle(DynamicConfigEvent event, String cacheName) {
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.resetRouteRule(Collections.emptyMap());
            RuleUtils.initKeys(configuration);
            RouterEventCollector.getInstance()
                    .collectServiceRouteRuleEvent(JSON.toJSONString(configuration.getRouteRule()));
            return;
        }
        Map<String, List<Object>> routeRuleMap = getRouteRuleMap(event);
        Map<String, List<EntireRule>> routeRule = new HashMap<>();
        for (Entry<String, List<Object>> entry : routeRuleMap.entrySet()) {
            List<Object> value = entry.getValue();
            if (CollectionUtils.isEmpty(value)) {
                continue;
            }
            List<EntireRule> list = JSONArray.parseArray(JSONObject.toJSONString(value), EntireRule.class);
            RuleUtils.removeInvalidRules(list, RouterConstant.DUBBO_CACHE_NAME.equals(cacheName),
                    RouterConstant.DUBBO_CACHE_NAME.equals(cacheName));
            if (!CollectionUtils.isEmpty(list)) {
                for (EntireRule rule : list) {
                    rule.getRules().sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
                }
                routeRule.put(entry.getKey(), list);
            }
        }
        configuration.resetRouteRule(routeRule);
        RuleUtils.initKeys(configuration);
        RouterEventCollector.getInstance()
                .collectServiceRouteRuleEvent(JSON.toJSONString(configuration.getRouteRule()));
    }

    @Override
    public boolean shouldHandle(String key) {
        return super.shouldHandle(key) && RouterConstant.ROUTER_KEY_PREFIX.equals(key);
    }

    private Map<String, List<Object>> getRouteRuleMap(DynamicConfigEvent event) {
        String content = event.getContent();
        Map<String, List<Object>> routeRuleMap = yaml.load(content);
        if (CollectionUtils.isEmpty(routeRuleMap)) {
            return Collections.emptyMap();
        }
        return routeRuleMap;
    }
}