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

package com.huaweicloud.sermant.router.config.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 判断实例是否匹配的策略
 *
 * @param <T> 泛型
 * @author provenceee
 * @since 2021-12-08
 */
public interface InstanceStrategy<T> {
    /**
     * 判断实例是否匹配
     *
     * @param instance 实例
     * @param tags 标签
     * @param mapper 获取metadata的方法
     * @return 是否匹配
     */
    boolean isMatch(T instance, List<Map<String, String>> tags, Function<T, Map<String, String>> mapper);
}