/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Based on org/apache/servicecomb/governance/marker/TrafficMarker.java from the Apache ServiceComb Java Chassis
 * project.
 */

package com.huawei.flowcontrol.common.adapte.cse.match;

import com.huawei.flowcontrol.common.adapte.cse.rule.Configurable;
import com.huawei.flowcontrol.common.entity.RequestEntity;

import java.util.List;

/**
 * 业务场景匹配
 *
 * @author zhouss
 * @since 2021-11-16
 */
public class BusinessMatcher extends Configurable implements Matcher {
    /**
     * 配置名
     */
    private String name;

    /**
     * 该业务场景的所有匹配器
     */
    private List<RequestMatcher> matches;

    @Override
    public boolean isInValid() {
        return matches == null || matches.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<RequestMatcher> getMatches() {
        return matches;
    }

    public void setMatches(List<RequestMatcher> matches) {
        this.matches = matches;
    }

    /**
     * 是否匹配
     *
     * 匹配规则如下: 有一个业务场景匹配，即匹配成功
     *
     * @param requestEntity 请求体
     * @return 是否匹配
     */
    @Override
    public boolean match(RequestEntity requestEntity) {
        if (requestEntity.getMethod() == null) {
            return false;
        }
        if (matches == null) {
            return false;
        }
        for (RequestMatcher matcher : matches) {
            // 有一个场景匹配成功，则满足要求
            if (matcher.match(requestEntity)) {
                return true;
            }
        }
        return false;
    }
}
