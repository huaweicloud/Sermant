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

package com.huaweicloud.sermant.router.config.strategy.match;

import com.huaweicloud.sermant.router.config.label.entity.MatchStrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 值匹配策略测试
 *
 * @author provenceee
 * @since 2021-12-01
 */
public class MatchStrategyTest {
    private MatchStrategy exact;

    private MatchStrategy regex;

    private MatchStrategy noEqu;

    private MatchStrategy noLess;

    private MatchStrategy noGreater;

    private MatchStrategy greater;

    private MatchStrategy less;

    private MatchStrategy in;

    private MatchStrategy prefix;

    private List<String> strValues;

    private List<String> intValues;

    private List<String> regexValues;

    private List<String> emptyList;

    private List<String> nullValueList;

    /**
     * 初始化
     */
    @Before
    public void before() {
        exact = MatchStrategy.EXACT;
        regex = MatchStrategy.REGEX;
        noEqu = MatchStrategy.NOEQU;
        noLess = MatchStrategy.NOLESS;
        noGreater = MatchStrategy.NOGREATER;
        greater = MatchStrategy.GREATER;
        less = MatchStrategy.LESS;
        in = MatchStrategy.IN;
        prefix = MatchStrategy.PREFIX;
        strValues = new ArrayList<>();
        strValues.add("foo");
        strValues.add("bar");
        intValues = new ArrayList<>();
        intValues.add("10");
        regexValues = new ArrayList<>();
        regexValues.add("^bar.*");
        emptyList = Collections.emptyList();
        nullValueList = Collections.singletonList(null);
    }

    /**
     * 等值匹配策略
     */
    @Test
    public void testExact() {
        // 正常情况，不区分大小写
        Assert.assertTrue(exact.isMatch(strValues, "FoO", false));

        // 正常情况，区分大小写
        Assert.assertTrue(exact.isMatch(strValues, "foo", true));

        // 测试大小写不一致
        Assert.assertFalse(exact.isMatch(strValues, "foO", true));

        // 测试null arg
        Assert.assertFalse(exact.isMatch(strValues, null, false));

        // 测试null arg
        Assert.assertFalse(exact.isMatch(strValues, null, true));

        // 测试null values
        Assert.assertFalse(exact.isMatch(null, "FoO", false));

        // 测试null values
        Assert.assertFalse(exact.isMatch(null, "foo", true));

        // 测试empty values
        Assert.assertFalse(exact.isMatch(emptyList, "FoO", false));

        // 测试empty values
        Assert.assertFalse(exact.isMatch(emptyList, "foo", true));

        // 测试values.get(0) == null
        Assert.assertFalse(exact.isMatch(nullValueList, "FoO", false));

        // 测试values.get(0) == null
        Assert.assertFalse(exact.isMatch(nullValueList, "foo", true));
    }

    /**
     * 正则表达式匹配策略
     */
    @Test
    public void testRegex() {
        // 正常情况，不区分大小写
        Assert.assertTrue(regex.isMatch(regexValues, "BaR", false));

        // 正常情况，区分大小写
        Assert.assertTrue(regex.isMatch(regexValues, "bar", true));

        // 测试大小写不一致
        Assert.assertFalse(regex.isMatch(regexValues, "Bar", true));

        // 测试null arg
        Assert.assertFalse(regex.isMatch(regexValues, null, false));

        // 测试null arg
        Assert.assertFalse(regex.isMatch(regexValues, null, true));

        // 测试null values
        Assert.assertFalse(regex.isMatch(null, "BaR", false));

        // 测试null values
        Assert.assertFalse(regex.isMatch(null, "bar", true));

        // 测试empty values
        Assert.assertFalse(regex.isMatch(emptyList, "BaR", false));

        // 测试empty values
        Assert.assertFalse(regex.isMatch(emptyList, "bar", true));

        // 测试values.get(0) == null
        Assert.assertFalse(regex.isMatch(nullValueList, "BaR", false));

        // 测试values.get(0) == null
        Assert.assertFalse(regex.isMatch(nullValueList, "bar", true));
    }

    /**
     * 不等于匹配策略
     */
    @Test
    public void testNoEqu() {
        // 正常情况，不区分大小写
        Assert.assertFalse(noEqu.isMatch(strValues, "FoO", false));

        // 正常情况，不区分大小写
        Assert.assertTrue(noEqu.isMatch(strValues, "BaR", false));

        // 正常情况，区分大小写
        Assert.assertFalse(noEqu.isMatch(strValues, "foo", true));

        // 测试大小写不一致
        Assert.assertTrue(noEqu.isMatch(strValues, "Foo", true));

        // 测试null arg
        Assert.assertFalse(noEqu.isMatch(strValues, null, false));

        // 测试null arg
        Assert.assertFalse(noEqu.isMatch(strValues, null, true));

        // 测试null values
        Assert.assertFalse(noEqu.isMatch(null, "FoO", false));

        // 测试null values
        Assert.assertFalse(noEqu.isMatch(null, "foo", true));

        // 测试empty values
        Assert.assertFalse(noEqu.isMatch(emptyList, "FoO", false));

        // 测试empty values
        Assert.assertFalse(noEqu.isMatch(emptyList, "foo", true));

        // 测试values.get(0) == null
        Assert.assertFalse(noEqu.isMatch(nullValueList, "FoO", false));

        // 测试values.get(0) == null
        Assert.assertFalse(noEqu.isMatch(nullValueList, "foo", true));
    }

    /**
     * 不小于匹配策略
     */
    @Test
    public void testNoLess() {
        // 正常情况，等于，不区分大小写
        Assert.assertTrue(noLess.isMatch(intValues, "10", false));

        // 正常情况，等于，区分大小写
        Assert.assertTrue(noLess.isMatch(intValues, "10", true));

        // 正常情况，大于，不区分大小写
        Assert.assertTrue(noLess.isMatch(intValues, "11", false));

        // 正常情况，大于，区分大小写
        Assert.assertTrue(noLess.isMatch(intValues, "11", true));

        // 测试小于，不区分大小写
        Assert.assertFalse(noLess.isMatch(intValues, "9", false));

        // 测试小于，区分大小写
        Assert.assertFalse(noLess.isMatch(intValues, "9", true));

        // 测试非数字
        Assert.assertFalse(noLess.isMatch(intValues, "foo", false));

        // 测试非数字
        Assert.assertFalse(noLess.isMatch(intValues, "foo", true));

        // 测试null arg
        Assert.assertFalse(noLess.isMatch(intValues, null, false));

        // 测试null arg
        Assert.assertFalse(noLess.isMatch(intValues, null, true));

        // 测试null values
        Assert.assertFalse(noLess.isMatch(null, "10", false));

        // 测试null values
        Assert.assertFalse(noLess.isMatch(null, "10", true));

        // 测试empty values
        Assert.assertFalse(noLess.isMatch(emptyList, "10", false));

        // 测试empty values
        Assert.assertFalse(noLess.isMatch(emptyList, "10", true));

        // 测试values.get(0) == null
        Assert.assertFalse(noLess.isMatch(nullValueList, "10", false));

        // 测试values.get(0) == null
        Assert.assertFalse(noLess.isMatch(nullValueList, "10", true));
    }

    /**
     * 不大于匹配策略
     */
    @Test
    public void testNoGreater() {
        // 正常情况，等于，不区分大小写
        Assert.assertTrue(noGreater.isMatch(intValues, "10", false));

        // 正常情况，等于，区分大小写
        Assert.assertTrue(noGreater.isMatch(intValues, "10", true));

        // 正常情况，小于，不区分大小写
        Assert.assertTrue(noGreater.isMatch(intValues, "9", false));

        // 正常情况，小于，区分大小写
        Assert.assertTrue(noGreater.isMatch(intValues, "9", true));

        // 测试大于，不区分大小写
        Assert.assertFalse(noGreater.isMatch(intValues, "11", false));

        // 测试大于，区分大小写
        Assert.assertFalse(noGreater.isMatch(intValues, "11", true));

        // 测试非数字
        Assert.assertFalse(noGreater.isMatch(intValues, "foo", false));

        // 测试非数字
        Assert.assertFalse(noGreater.isMatch(intValues, "foo", true));

        // 测试null arg
        Assert.assertFalse(noGreater.isMatch(intValues, null, false));

        // 测试null arg
        Assert.assertFalse(noGreater.isMatch(intValues, null, true));

        // 测试null values
        Assert.assertFalse(noGreater.isMatch(null, "10", false));

        // 测试null values
        Assert.assertFalse(noGreater.isMatch(null, "10", true));

        // 测试empty values
        Assert.assertFalse(noGreater.isMatch(emptyList, "10", false));

        // 测试empty values
        Assert.assertFalse(noGreater.isMatch(emptyList, "10", true));

        // 测试values.get(0) == null
        Assert.assertFalse(noGreater.isMatch(nullValueList, "10", false));

        // 测试values.get(0) == null
        Assert.assertFalse(noGreater.isMatch(nullValueList, "10", true));
    }

    /**
     * 大于匹配策略
     */
    @Test
    public void testGreater() {
        // 正常情况，大于，不区分大小写
        Assert.assertTrue(greater.isMatch(intValues, "11", false));

        // 正常情况，大于，区分大小写
        Assert.assertTrue(greater.isMatch(intValues, "11", true));

        // 测试等于，不区分大小写
        Assert.assertFalse(greater.isMatch(intValues, "10", false));

        // 测试等于，小于，区分大小写
        Assert.assertFalse(greater.isMatch(intValues, "10", true));

        // 测试小于，不区分大小写
        Assert.assertFalse(greater.isMatch(intValues, "9", false));

        // 测试小于，区分大小写
        Assert.assertFalse(greater.isMatch(intValues, "9", true));

        // 测试非数字
        Assert.assertFalse(greater.isMatch(intValues, "foo", false));

        // 测试非数字
        Assert.assertFalse(greater.isMatch(intValues, "foo", true));

        // 测试null arg
        Assert.assertFalse(greater.isMatch(intValues, null, false));

        // 测试null arg
        Assert.assertFalse(greater.isMatch(intValues, null, true));

        // 测试null values
        Assert.assertFalse(greater.isMatch(null, "11", false));

        // 测试null values
        Assert.assertFalse(greater.isMatch(null, "11", true));

        // 测试empty values
        Assert.assertFalse(greater.isMatch(emptyList, "11", false));

        // 测试empty values
        Assert.assertFalse(greater.isMatch(emptyList, "11", true));

        // 测试values.get(0) == null
        Assert.assertFalse(greater.isMatch(nullValueList, "11", false));

        // 测试values.get(0) == null
        Assert.assertFalse(greater.isMatch(nullValueList, "11", true));
    }

    /**
     * 小于匹配策略
     */
    @Test
    public void testLess() {
        // 正常情况，小于，不区分大小写
        Assert.assertTrue(less.isMatch(intValues, "9", false));

        // 正常情况，小于，区分大小写
        Assert.assertTrue(less.isMatch(intValues, "9", true));

        // 测试等于，不区分大小写
        Assert.assertFalse(less.isMatch(intValues, "10", false));

        // 测试等于，小于，区分大小写
        Assert.assertFalse(less.isMatch(intValues, "10", true));

        // 测试大于，不区分大小写
        Assert.assertFalse(less.isMatch(intValues, "11", false));

        // 测试大于，区分大小写
        Assert.assertFalse(less.isMatch(intValues, "11", true));

        // 测试非数字
        Assert.assertFalse(less.isMatch(intValues, "foo", false));

        // 测试非数字
        Assert.assertFalse(less.isMatch(intValues, "foo", true));

        // 测试null arg
        Assert.assertFalse(less.isMatch(intValues, null, false));

        // 测试null arg
        Assert.assertFalse(less.isMatch(intValues, null, true));

        // 测试null values
        Assert.assertFalse(less.isMatch(null, "9", false));

        // 测试null values
        Assert.assertFalse(less.isMatch(null, "9", true));

        // 测试empty values
        Assert.assertFalse(less.isMatch(emptyList, "9", false));

        // 测试empty values
        Assert.assertFalse(less.isMatch(emptyList, "9", true));

        // 测试values.get(0) == null
        Assert.assertFalse(less.isMatch(nullValueList, "9", false));

        // 测试values.get(0) == null
        Assert.assertFalse(less.isMatch(nullValueList, "9", true));
    }

    /**
     * 包含匹配策略
     */
    @Test
    public void testIn() {
        // 正常情况，不区分大小写
        Assert.assertTrue(in.isMatch(strValues, "BaR", false));

        // 正常情况，区分大小写
        Assert.assertTrue(in.isMatch(strValues, "foo", true));

        // 测试大小写不一致
        Assert.assertFalse(in.isMatch(strValues, "foO", true));

        // 测试null arg
        Assert.assertFalse(in.isMatch(strValues, null, false));

        // 测试null arg
        Assert.assertFalse(in.isMatch(strValues, null, true));

        // 测试null values
        Assert.assertFalse(in.isMatch(null, "BaR", false));

        // 测试null values
        Assert.assertFalse(in.isMatch(null, "foo", true));

        // 测试empty values
        Assert.assertFalse(in.isMatch(emptyList, "BaR", false));

        // 测试empty values
        Assert.assertFalse(in.isMatch(emptyList, "foo", true));

        // 测试values.get(0) == null
        Assert.assertFalse(in.isMatch(nullValueList, "BaR", false));

        // 测试values.get(0) == null
        Assert.assertFalse(in.isMatch(nullValueList, "foo", true));
    }

    /**
     * 前缀匹配策略
     */
    @Test
    public void testPrefix() {
        // 正常情况，不区分大小写
        Assert.assertTrue(prefix.isMatch(strValues, "FoOBar", false));

        // 正常情况，区分大小写
        Assert.assertTrue(prefix.isMatch(strValues, "fooBar", true));

        // 测试大小写不一致
        Assert.assertFalse(prefix.isMatch(strValues, "foOBar", true));

        // 测试null arg
        Assert.assertFalse(prefix.isMatch(strValues, null, false));

        // 测试null arg
        Assert.assertFalse(prefix.isMatch(strValues, null, true));

        // 测试null values
        Assert.assertFalse(prefix.isMatch(null, "FoOBar", false));

        // 测试null values
        Assert.assertFalse(prefix.isMatch(null, "fooBar", true));

        // 测试empty values
        Assert.assertFalse(prefix.isMatch(emptyList, "FoOBar", false));

        // 测试empty values
        Assert.assertFalse(prefix.isMatch(emptyList, "fooBar", true));

        // 测试values.get(0) == null
        Assert.assertFalse(prefix.isMatch(nullValueList, "FoOBar", false));

        // 测试values.get(0) == null
        Assert.assertFalse(prefix.isMatch(nullValueList, "fooBar", true));
    }
}