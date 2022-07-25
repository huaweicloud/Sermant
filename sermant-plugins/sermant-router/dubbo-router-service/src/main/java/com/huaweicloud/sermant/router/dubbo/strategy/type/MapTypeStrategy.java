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

package com.huaweicloud.sermant.router.dubbo.strategy.type;

import com.huaweicloud.sermant.router.dubbo.strategy.TypeStrategy;

import java.util.Map;
import java.util.Optional;

/**
 * map匹配策略
 *
 * @author provenceee
 * @since 2021-10-13
 */
public class MapTypeStrategy extends TypeStrategy {
    @Override
    public Optional<String> getValue(Object arg, String type) {
        if (arg instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) arg;
            Object object = map.get(getKey(type));
            return object == null ? Optional.empty() : Optional.of(String.valueOf(object));
        }
        return Optional.empty();
    }

    @Override
    public boolean isMatch(String type) {
        return checkType(type);
    }

    @Override
    public String getBeginFlag() {
        return ".get(\"";
    }

    @Override
    public String getEndFlag() {
        return "\")";
    }
}