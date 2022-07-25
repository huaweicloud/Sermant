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

package com.huaweicloud.sermant.router.dubbo.strategy.type;

import com.huaweicloud.sermant.router.dubbo.strategy.TypeStrategy;

import org.junit.Assert;
import org.junit.Test;

/**
 * 空匹配策略测试
 *
 * @author provenceee
 * @since 2021-12-01
 */
public class EmptyTypeStrategyTest {
    /**
     * 测试空策略
     */
    @Test
    public void testValue() {
        TypeStrategy strategy = new EmptyTypeStrategy();

        // 正常情况
        Assert.assertEquals("foo", strategy.getValue("foo", "").orElse(null));

        // 正常情况
        Assert.assertEquals("foo", strategy.getValue("foo", null).orElse(null));

        // 测试不等于
        Assert.assertNotEquals("foo", strategy.getValue("bar", null).orElse(null));

        // 测试null
        Assert.assertNotEquals("foo", strategy.getValue(null, null).orElse(null));
    }
}