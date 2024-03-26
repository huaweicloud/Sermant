/*
 *
 *  * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.huaweicloud.sermant.router.config.handler.kind;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.event.RouterEventCollector;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.common.SafeConstructor;
import com.huaweicloud.sermant.router.config.entity.EntireRule;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.entity.Rule;
import com.huaweicloud.sermant.router.config.handler.AbstractHandler;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 配置类型处理器（兼容1.0.x版本使用）
 *
 * @author provenceee
 * @since 2024-01-11
 */
public abstract class AbstractKindHandler implements AbstractHandler {
    private static final RouterConfig ROUTER_CONFIG = PluginConfigManager.getPluginConfig(RouterConfig.class);

    /**
     * yaml
     */
    protected final Yaml yaml;

    private final String prefix;

    private final String kind;

    /**
     * 构造方法
     *
     * @param prefix 服务级key的前缀，用来截取服务名
     * @param kind 配置类型
     */
    public AbstractKindHandler(String prefix, String kind) {
        this.yaml = new Yaml(new SafeConstructor(null));
        this.prefix = prefix;
        this.kind = kind;
    }

    /**
     * 路由配置类型处理
     *
     * @param event 配置监听事件
     * @param cacheName 缓存名
     */
    @Override
    public void handle(DynamicConfigEvent event, String cacheName) {
        if (RouterConstant.GLOBAL_COMPATIBILITY_KEY_LIST.contains(event.getKey())) {
            handleGlobalRules(event, cacheName);
        } else if (RouterConstant.COMPATIBILITY_KEY_LIST.contains(event.getKey())) {
            handleRouterRules(event, cacheName);
        } else {
            handleServiceRules(event, cacheName);
        }
    }

    /**
     * 是否需要兼容性处理
     *
     * @param key 配置key
     * @return 是否需要处理
     */
    @Override
    public boolean shouldHandle(String key) {
        // return compatibilityEnabled switch value
        return ROUTER_CONFIG.isEnabledPreviousRule();
    }

    /**
     * 解析全局配置
     *
     * @param event 事件
     * @param cacheName 缓存key
     */
    private void handleGlobalRules(DynamicConfigEvent event, String cacheName) {
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.resetGlobalRule(getEntireRule(Collections.emptyList()));
            RuleUtils.initMatchKeys(configuration);
            RouterEventCollector.getInstance()
                    .collectGlobalRouteRuleEvent(JSON.toJSONString(configuration.getGlobalRule()));
            return;
        }

        List<Rule> list = JSONArray.parseArray(JSONObject.toJSONString(getGlobalRules(event)), Rule.class);
        RuleUtils.removeInvalidRules(kind, list, RouterConstant.DUBBO_CACHE_NAME.equals(cacheName),
                RouterConstant.DUBBO_CACHE_NAME.equals(cacheName));
        if (!CollectionUtils.isEmpty(list)) {
            list.sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
        }
        configuration.resetGlobalRule(getEntireRule(list));
        RuleUtils.initMatchKeys(configuration);
        RouterEventCollector.getInstance()
                .collectGlobalRouteRuleEvent(JSON.toJSONString(configuration.getGlobalRule()));
    }

    /**
     * 解析服务级配置（全集）
     *
     * @param event 事件
     * @param cacheName 缓存key
     */
    private void handleRouterRules(DynamicConfigEvent event, String cacheName) {
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.resetRouteRule(kind, Collections.emptyMap());
            RuleUtils.initKeys(configuration);
            RouterEventCollector.getInstance()
                    .collectServiceRouteRuleEvent(JSON.toJSONString(configuration.getRouteRule()));
            return;
        }
        Map<String, String> routeRuleMap = getRouteRuleMap(event);
        Map<String, EntireRule> routeRule = new HashMap<>();
        for (Entry<String, String> entry : routeRuleMap.entrySet()) {
            List<Map<String, String>> routeRuleList = yaml.load(entry.getValue());
            if (CollectionUtils.isEmpty(routeRuleList)) {
                continue;
            }
            List<Rule> list = JSONArray.parseArray(JSONObject.toJSONString(routeRuleList), Rule.class);
            RuleUtils.removeInvalidRules(kind, list, RouterConstant.DUBBO_CACHE_NAME.equals(cacheName),
                    RouterConstant.DUBBO_CACHE_NAME.equals(cacheName));
            if (!CollectionUtils.isEmpty(list)) {
                list.sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
                routeRule.put(entry.getKey(), getEntireRule(list));
            }
        }
        configuration.resetRouteRule(kind, routeRule);
        RuleUtils.initKeys(configuration);
        RouterEventCollector.getInstance()
                .collectServiceRouteRuleEvent(JSON.toJSONString(configuration.getRouteRule()));
    }

    /**
     * 解析服务级配置
     *
     * @param event 事件
     * @param cacheName 缓存key
     */
    private void handleServiceRules(DynamicConfigEvent event, String cacheName) {
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        String serviceName = event.getKey().substring(prefix.length() + 1);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.removeServiceRule(serviceName, kind);
            RuleUtils.initKeys(configuration);
            RouterEventCollector.getInstance()
                    .collectServiceRouteRuleEvent(JSON.toJSONString(configuration.getRouteRule()));
            return;
        }
        List<Rule> list = JSONArray.parseArray(JSONObject.toJSONString(getServiceRules(event, serviceName)),
                Rule.class);
        RuleUtils.removeInvalidRules(kind, list, RouterConstant.DUBBO_CACHE_NAME.equals(cacheName),
                RouterConstant.DUBBO_CACHE_NAME.equals(cacheName));
        if (CollectionUtils.isEmpty(list)) {
            configuration.removeServiceRule(serviceName, kind);
        } else {
            list.sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
            configuration.updateServiceRule(serviceName, getEntireRule(list));
        }
        RuleUtils.initKeys(configuration);
        RouterEventCollector.getInstance()
                .collectServiceRouteRuleEvent(JSON.toJSONString(configuration.getRouteRule()));
    }

    private EntireRule getEntireRule(List<Rule> rules) {
        EntireRule entireRule = new EntireRule();
        entireRule.setKind(kind);
        entireRule.setRules(rules == null ? Collections.emptyList() : rules);
        return entireRule;
    }

    private List<Map<String, Object>> getGlobalRules(DynamicConfigEvent event) {
        String content = event.getContent();
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        Map<String, List<Map<String, Object>>> map = yaml.load(content);
        return map.get(RouterConstant.GLOBAL_ROUTER_KEY);
    }

    private List<Map<String, Object>> getServiceRules(DynamicConfigEvent event, String serviceName) {
        String content = event.getContent();
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        Map<String, List<Map<String, Object>>> map = yaml.load(content);
        return map.get(prefix + RouterConstant.POINT + serviceName);
    }

    private Map<String, String> getRouteRuleMap(DynamicConfigEvent event) {
        String content = event.getContent();
        Map<String, String> routeRuleMap = yaml.load(content);
        if (CollectionUtils.isEmpty(routeRuleMap)) {
            return Collections.emptyMap();
        }
        return routeRuleMap;
    }
}
