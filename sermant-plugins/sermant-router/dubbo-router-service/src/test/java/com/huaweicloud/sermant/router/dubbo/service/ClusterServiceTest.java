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

package com.huaweicloud.sermant.router.dubbo.service;

import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.dubbo.cache.DubboCache;

import com.alibaba.dubbo.common.URL;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试ClusterServiceImpl
 *
 * @author provenceee
 * @since 2022-03-21
 */
public class ClusterServiceTest {
    private static final URL ALIBABA_URL =
        URL.valueOf("dubbo://localhost:8080/com.huawei.foo.BarTest?bar=foo&application=bar");

    private static final org.apache.dubbo.common.URL APACHE_URL = org.apache.dubbo.common.URL
        .valueOf("dubbo://localhost:8081/com.huawei.foo.FooTest?foo=bar&application=foo");

    private static final int EXPECT_LENGTH = 2;

    private final ClusterService clusterService;

    private final Map<String, String> map = new HashMap<>();

    /**
     * 构造方法
     */
    public ClusterServiceTest() {
        clusterService = new ClusterServiceImpl();
        map.put(RouterConstant.TAG_VERSION_KEY, "0.0.1");
        map.put(RouterConstant.ROUTER_LDC_KEY, "ldc1");
    }

    /**
     * 测试alibaba url
     *
     * @see com.alibaba.dubbo.common.URL
     */
    @Test
    public void testAlibaba() {
        // 数组长度不匹配
        Object[] arguments = new Object[1];
        arguments[0] = ALIBABA_URL;
        clusterService.doBefore(arguments);
        Assert.assertNull(DubboCache.INSTANCE.getApplication("com.huawei.foo.BarTest"));

        // 正常
        arguments = new Object[EXPECT_LENGTH];
        arguments[0] = ALIBABA_URL;
        arguments[1] = map;
        clusterService.doBefore(arguments);
        Assert.assertNull(((Map<?, ?>) arguments[1]).get(RouterConstant.TAG_VERSION_KEY));
        Assert.assertNull(((Map<?, ?>) arguments[1]).get(RouterConstant.ROUTER_LDC_KEY));
        Assert.assertEquals("bar", DubboCache.INSTANCE.getApplication("com.huawei.foo.BarTest"));

        // arguments[1]为null
        arguments[1] = null;
        clusterService.doBefore(arguments);
        Assert.assertNull(arguments[1]);
    }

    /**
     * 测试apache url
     *
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testApache() {
        // 数组长度不匹配
        Object[] arguments = new Object[1];
        arguments[0] = APACHE_URL;
        clusterService.doBefore(arguments);
        Assert.assertNull(DubboCache.INSTANCE.getApplication("com.huawei.foo.FooTest"));

        // 正常
        arguments = new Object[EXPECT_LENGTH];
        arguments[0] = APACHE_URL;
        arguments[1] = map;
        clusterService.doBefore(arguments);
        Assert.assertNull(((Map<?, ?>) arguments[1]).get(RouterConstant.TAG_VERSION_KEY));
        Assert.assertNull(((Map<?, ?>) arguments[1]).get(RouterConstant.ROUTER_LDC_KEY));
        Assert.assertEquals("foo", DubboCache.INSTANCE.getApplication("com.huawei.foo.FooTest"));

        // arguments[1]为null
        arguments[1] = null;
        clusterService.doBefore(arguments);
        Assert.assertNull(arguments[1]);
    }
}