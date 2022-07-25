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

import com.huaweicloud.sermant.router.config.strategy.ValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.ExactValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.GreaterValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.InValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.LessValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.NoEquValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.NoGreaterValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.NoLessValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.PrefixValueMatchStrategy;
import com.huaweicloud.sermant.router.config.strategy.match.RegexValueMatchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * 值匹配策略
 *
 * @author provenceee
 * @since 2021-10-14
 */
public enum MatchStrategy {
    /**
     * 等值匹配
     */
    EXACT(new ExactValueMatchStrategy()),

    /**
     * 正则表达式匹配
     */
    REGEX(new RegexValueMatchStrategy()),

    /**
     * 不等于匹配
     */
    NOEQU(new NoEquValueMatchStrategy()),

    /**
     * 不小于匹配
     */
    NOLESS(new NoLessValueMatchStrategy()),

    /**
     * 不大于匹配
     */
    NOGREATER(new NoGreaterValueMatchStrategy()),

    /**
     * 大于匹配
     */
    GREATER(new GreaterValueMatchStrategy()),

    /**
     * 小于匹配
     */
    LESS(new LessValueMatchStrategy()),

    /**
     * 包含匹配
     */
    IN(new InValueMatchStrategy()),

    /**
     * 前缀匹配
     */
    PREFIX(new PrefixValueMatchStrategy());

    private final ValueMatchStrategy valueMatchStrategy;

    MatchStrategy(ValueMatchStrategy valueMatchStrategy) {
        this.valueMatchStrategy = valueMatchStrategy;
    }

    /**
     * 是否匹配
     *
     * @param values 期望值
     * @param arg 参数值
     * @param isCaseInsensitive 是否区分大小写
     * @return 是否匹配
     */
    public boolean isMatch(List<String> values, String arg, boolean isCaseInsensitive) {
        if (isCaseInsensitive || values == null || arg == null) {
            return valueMatchStrategy.isMatch(values, arg);
        }

        // 如果大小写不敏感，则统一转成大写
        List<String> list = new ArrayList<>(values);
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            String next = listIterator.next();
            if (next != null) {
                listIterator.set(next.toUpperCase(Locale.ROOT));
            }
        }
        return valueMatchStrategy.isMatch(list, arg.toUpperCase(Locale.ROOT));
    }
}