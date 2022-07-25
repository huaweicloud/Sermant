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

package com.huaweicloud.sermant.router.config.listener;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.subscribe.processor.OrderConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.label.LabelCache;
import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.label.entity.Rule;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * 配置监听器
 *
 * @author provenceee
 * @since 2021-11-29
 */
public class RouterConfigListener implements DynamicConfigListener {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final String cacheName;

    /**
     * 构造方法
     *
     * @param cacheName 缓存的标签名
     */
    public RouterConfigListener(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void process(DynamicConfigEvent event) {
        RouterConfiguration configuration = LabelCache.getLabel(cacheName);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            resetRouterConfiguration(configuration);
            return;
        }
        Map<String, String> routeRuleMap = getRouteRuleMap(event);
        Yaml routeRuleYaml = new Yaml();
        Map<String, List<Rule>> routeRule = new LinkedHashMap<>();
        for (Entry<String, String> entry : routeRuleMap.entrySet()) {
            List<Map<String, String>> routeRuleList = routeRuleYaml.load(entry.getValue());
            if (CollectionUtils.isEmpty(routeRuleList)) {
                continue;
            }
            List<Rule> list = JSONArray.parseArray(JSONObject.toJSONString(routeRuleList), Rule.class);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            for (Rule rule : list) {
                // 去掉无效的规则
                RuleUtils.removeInvalidRules(rule.getMatch());

                // 去掉无效的路由
                RuleUtils.removeInvalidRoute(rule.getRoute());
            }
            list.sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
            routeRule.put(entry.getKey(), list);
        }
        configuration.setRouteRule(routeRule);
        configuration.setValid(!CollectionUtils.isEmpty(routeRule));
        RuleUtils.initHeaderKeys(configuration);
        LOGGER.info(String.format(Locale.ROOT, "Config [%s] has been %s ", event.getKey(), event.getEventType()));
    }

    private void resetRouterConfiguration(RouterConfiguration configuration) {
        configuration.setRouteRule(Collections.emptyMap());
        configuration.setValid(false);
    }

    private Map<String, String> getRouteRuleMap(DynamicConfigEvent event) {
        if (event instanceof OrderConfigEvent) {
            Map<String, Object> allData = ((OrderConfigEvent) event).getAllData();
            String prefix =
                RouterConstant.ROUTER_CONFIG_SERVICECOMB_KEY + "." + RouterConstant.ROUTER_CONFIG_ROUTE_RULE_KEY + ".";
            Map<String, String> routeRuleMap = new HashMap<>();
            for (Entry<String, Object> entry : allData.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    routeRuleMap.put(entry.getKey().substring(prefix.length()), (String) entry.getValue());
                }
            }
            return routeRuleMap;
        }
        String content = event.getContent();
        Yaml yaml = new Yaml();
        Map<String, Map<String, Map<String, String>>> load = yaml.load(content);
        if (CollectionUtils.isEmpty(load)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, String>> servicecomb = load.get(RouterConstant.ROUTER_CONFIG_SERVICECOMB_KEY);
        if (CollectionUtils.isEmpty(servicecomb)) {
            return Collections.emptyMap();
        }
        return servicecomb.get(RouterConstant.ROUTER_CONFIG_ROUTE_RULE_KEY);
    }
}