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

package com.huaweicloud.sermant.router.config.label.entity;

import java.util.List;
import java.util.Map;

/**
 * 路由标签
 *
 * @author provenceee
 * @since 2021-10-27
 */
public class RouterConfiguration {
    /**
     * 是否有效
     */
    private boolean valid;

    /**
     * 当前应用标签
     */
    private CurrentTag currentTag;

    /**
     * 标签规则,key为应用名，value为规则
     */
    private Map<String, List<Rule>> routeRule;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public CurrentTag getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(CurrentTag currentTag) {
        this.currentTag = currentTag;
    }

    public Map<String, List<Rule>> getRouteRule() {
        return routeRule;
    }

    public void setRouteRule(Map<String, List<Rule>> routeRule) {
        this.routeRule = routeRule;
    }

    /**
     * 灰度标签是否无效
     *
     * @param configuration 灰度标签
     * @return 是否无效
     */
    public static boolean isInValid(RouterConfiguration configuration) {
        return configuration == null || !configuration.isValid();
    }
}