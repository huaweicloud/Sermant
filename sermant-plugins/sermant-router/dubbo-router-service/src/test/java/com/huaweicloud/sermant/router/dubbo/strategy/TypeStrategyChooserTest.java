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

package com.huaweicloud.sermant.router.dubbo.strategy;

import com.huaweicloud.sermant.core.common.CommonConstant;
import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.router.dubbo.strategy.type.Entity;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 规则策略选择器测试
 *
 * @author provenceee
 * @since 2021-12-01
 */
public class TypeStrategyChooserTest {
    private final TypeStrategyChooser chooser;

    private final Object[] arguments;

    /**
     * 构造方法
     */
    public TypeStrategyChooserTest() {
        chooser = TypeStrategyChooser.INSTANCE;
        arguments = new Object[1];
    }

    /**
     * 初始化
     */
    @BeforeClass
    public static void init() {
        Map<String, Object> map = new HashMap<>();
        map.put(CommonConstant.LOG_SETTING_FILE_KEY,
            TypeStrategyChooserTest.class.getResource("/logback-test.xml").getPath());
        LoggerFactory.init(map);
    }

    /**
     * 测试数组策略
     */
    @Test
    public void testArray() {
        arguments[0] = new String[]{"foo"};

        // 正常情况
        Assert.assertEquals("foo", chooser.getValue("[0]", "args0", arguments).orElse(null));
    }

    /**
     * 测试空策略
     */
    @Test
    public void testEmpty() {
        arguments[0] = "foo";

        // 正常情况
        Assert.assertEquals("foo", chooser.getValue("", "args0", arguments).orElse(null));
    }

    /**
     * 测试enabled策略
     */
    @Test
    public void testEnabled() {
        Entity entity = new Entity();
        entity.setEnabled(true);
        arguments[0] = entity;

        // 正常情况
        Assert.assertEquals(Boolean.TRUE.toString(), chooser.getValue(".isEnabled()", "args0", arguments).orElse(null));
    }

    /**
     * 测试列表策略
     */
    @Test
    public void testList() {
        arguments[0] = Collections.singletonList("foo");

        // 正常情况
        Assert.assertEquals("foo", chooser.getValue(".get(0)", "args0", arguments).orElse(null));
    }

    /**
     * 测试map策略
     */
    @Test
    public void testMap() {
        arguments[0] = Collections.singletonMap("foo", "bar");

        // 正常情况
        Assert.assertEquals("bar", chooser.getValue(".get(\"foo\")", "args0", arguments).orElse(null));
    }

    /**
     * 测试实体策略
     */
    @Test
    public void testObject() {
        Entity entity = new Entity();
        entity.setTest("foo");
        arguments[0] = entity;

        // 正常情况
        Assert.assertEquals("foo", chooser.getValue(".test", "args0", arguments).orElse(null));
    }

    /**
     * 测试null（异常情况）
     */
    @Test
    public void testNull() {
        // 参数为null
        Assert.assertNull(chooser.getValue(".test", "args0", null).orElse(null));

        // 未命中TypeStrategy
        Assert.assertNull(chooser.getValue("bar", "args0", arguments).orElse(null));

        // 非数字
        Assert.assertNull(chooser.getValue(".test", "argsA", arguments).orElse(null));

        // 索引越界
        Assert.assertNull(chooser.getValue(".test", "args1", arguments).orElse(null));
    }

    /**
     * 测试非数字
     */
    @Test
    public void testInvalidNumber() {
        arguments[0] = new String[]{"foo"};

        // 测试非数字
        Assert.assertNull(chooser.getValue("[bar]", "args0", arguments).orElse(null));
    }
}