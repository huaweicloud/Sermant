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

import com.huaweicloud.sermant.core.common.CommonConstant;
import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.router.dubbo.strategy.TypeStrategy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体匹配策略测试
 *
 * @author provenceee
 * @since 2021-12-01
 */
public class ObjectTypeStrategyTest {
    /**
     * 初始化
     */
    @BeforeClass
    public static void init() {
        Map<String, Object> map = new HashMap<>();
        map.put(CommonConstant.LOG_SETTING_FILE_KEY,
            ObjectTypeStrategyTest.class.getResource("/logback-test.xml").getPath());
        LoggerFactory.init(map);
    }

    /**
     * 测试实体策略
     */
    @Test
    public void testValue() {
        TypeStrategy strategy = new ObjectTypeStrategy();
        Entity entity = new Entity();
        entity.setTest("bar");

        // 正常情况
        Assert.assertEquals("bar", strategy.getValue(entity, ".test").orElse(null));

        // 测试null
        Assert.assertNotEquals("bar", strategy.getValue(new Entity(), ".test").orElse(null));

        // 测试找不到字段
        Assert.assertNull(strategy.getValue(new Entity(), ".foo").orElse(null));

        // 测试不等于
        entity.setTest("foo");
        Assert.assertNotEquals("bar", strategy.getValue(entity, ".test").orElse(null));
    }
}