/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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
import com.huaweicloud.sermant.core.utils.StringUtils;
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
import java.util.List;
import java.util.Map;

/**
 * 路由配置处理器(全局维度)
 *
 * @author lilai
 * @since 2023-02-18
 */
public class GlobalConfigHandler extends AbstractConfigHandler {
    @Override
    public void handle(DynamicConfigEvent event, String cacheName) {
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.resetGlobalRule(Collections.emptyList());
            RuleUtils.initMatchKeys(configuration);
            RouterEventCollector.getInstance()
                    .collectGlobalRouteRuleEvent(JSON.toJSONString(configuration.getGlobalRule()));
            return;
        }
        List<EntireRule> list = JSONArray.parseArray(JSONObject.toJSONString(getRule(event)), EntireRule.class);
        RuleUtils.removeInvalidRules(list, RouterConstant.DUBBO_CACHE_NAME.equals(cacheName),
                RouterConstant.DUBBO_CACHE_NAME.equals(cacheName));
        if (CollectionUtils.isEmpty(list)) {
            configuration.resetGlobalRule(Collections.emptyList());
        } else {
            for (EntireRule rule : list) {
                rule.getRules().sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
            }
            configuration.resetGlobalRule(list);
        }
        RuleUtils.initMatchKeys(configuration);
        RouterEventCollector.getInstance()
                .collectGlobalRouteRuleEvent(JSON.toJSONString(configuration.getGlobalRule()));
    }

    @Override
    public boolean shouldHandle(String key) {
        return super.shouldHandle(key) && RouterConstant.GLOBAL_ROUTER_KEY.equals(key);
    }

    private List<Map<String, Object>> getRule(DynamicConfigEvent event) {
        String content = event.getContent();
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        Map<String, List<Map<String, Object>>> map = yaml.load(content);
        return map.get(RouterConstant.GLOBAL_ROUTER_KEY);
    }
}
