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

package com.huaweicloud.sermant.router.spring.service;

import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.label.LabelCache;
import com.huaweicloud.sermant.router.config.label.entity.Route;
import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.label.entity.Rule;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;
import com.huaweicloud.sermant.router.spring.cache.AppCache;
import com.huaweicloud.sermant.router.spring.strategy.RuleStrategyHandler;
import com.huaweicloud.sermant.router.spring.utils.RouteUtils;

import java.util.List;
import java.util.Map;

/**
 * BaseLoadBalancerInterceptor服务
 *
 * @author provenceee
 * @since 2022-07-20
 */
public class LoadBalancerServiceImpl implements LoadBalancerService {
    @Override
    public List<Object> getTargetInstances(String targetName, List<Object> serverList, String path,
        Map<String, List<String>> header) {
        RouterConfiguration configuration = LabelCache.getLabel(RouterConstant.SPRING_CACHE_NAME);
        List<Rule> rules = RuleUtils.getRules(configuration, targetName, path, AppCache.INSTANCE.getAppName());
        List<Route> routes = RouteUtils.getRoutes(rules, header);
        if (!CollectionUtils.isEmpty(routes)) {
            return RuleStrategyHandler.INSTANCE.getTargetInstances(routes, serverList);
        }
        return RuleStrategyHandler.INSTANCE.getMismatchInstances(RuleUtils.getTags(rules), serverList);
    }
}
