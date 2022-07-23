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

package com.huaweicloud.sermant.router.dubbo.strategy.instance;

import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.config.strategy.AbstractInstanceStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 匹配不在mismatch中的invoker
 *
 * @author provenceee
 * @since 2021-12-08
 */
public class MismatchInstanceStrategy extends AbstractInstanceStrategy<Object> {
    /**
     * 匹配不在mismatch中的invoker
     *
     * @param invoker Invoker
     * @param tags 没有匹配上的标签
     * @return 是否匹配
     */
    @Override
    public boolean isMatch(Object invoker, List<Map<String, String>> tags,
        Function<Object, Map<String, String>> mapper) {
        // 由于由于mismatch里面的标签已经匹配过了且没有匹配上，所以要剔除掉，不能参与负载均衡，否则会导致流量比例不正确（会偏高）
        String invokerVersion = getMetadata(invoker, mapper)
            .getOrDefault(RouterConstant.TAG_VERSION_KEY, RouterConstant.ROUTER_DEFAULT_VERSION);
        Set<String> mismatchVersions = new HashSet<>();
        for (Map<String, String> tag : tags) {
            if (StringUtils.isExist(tag.get(VERSION_KEY))) {
                mismatchVersions.add(tag.get(VERSION_KEY));
            }
        }
        return !mismatchVersions.contains(invokerVersion);
    }
}