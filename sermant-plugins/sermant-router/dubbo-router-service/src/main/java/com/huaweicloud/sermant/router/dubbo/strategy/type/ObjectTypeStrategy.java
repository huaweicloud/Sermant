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

import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.utils.ReflectUtils;
import com.huaweicloud.sermant.router.dubbo.strategy.TypeStrategy;

import java.util.Optional;

/**
 * 实体匹配策略
 *
 * @author provenceee
 * @since 2021-10-13
 */
public class ObjectTypeStrategy extends TypeStrategy {
    @Override
    public Optional<String> getValue(Object arg, String type) {
        return ReflectUtils.getFieldValue(arg, getKey(type)).map(String::valueOf);
    }

    @Override
    public boolean isMatch(String type) {
        if (StringUtils.isBlank(type) || !type.startsWith(".")) {
            return false;
        }
        return !type.contains("(") && !type.contains(")") && !type.contains("[") && !type.contains("]");
    }

    @Override
    public String getBeginFlag() {
        return ".";
    }

    @Override
    public String getEndFlag() {
        return "";
    }
}