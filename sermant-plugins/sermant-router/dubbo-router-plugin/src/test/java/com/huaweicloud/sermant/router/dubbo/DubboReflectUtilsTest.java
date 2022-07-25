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

package com.huaweicloud.sermant.router.dubbo;

import com.huaweicloud.sermant.core.common.CommonConstant;
import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.router.common.utils.ReflectUtils;
import com.huaweicloud.sermant.router.dubbo.utils.DubboReflectUtils;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射测试类
 *
 * @author provenceee
 * @since 2022-03-18
 */
public class DubboReflectUtilsTest {
    private static final URL ALIBABA_URL = URL.valueOf("dubbo://localhost:8080/com.huawei.foo.BarTest?bar=foo");

    private static final org.apache.dubbo.common.URL APACHE_URL = org.apache.dubbo.common.URL
        .valueOf("dubbo://localhost:8081/com.huawei.foo.FooTest?foo=bar");

    private static final String BAR = "bar";

    private static final String FOO = "foo";

    private final ApplicationConfig alibabaConfig;

    private final org.apache.dubbo.config.ApplicationConfig apacheConfig;

    /**
     * 构造方法
     */
    public DubboReflectUtilsTest() {
        alibabaConfig = new ApplicationConfig(BAR);
        DubboReflectUtils.setParameters(alibabaConfig, Collections.singletonMap(BAR, FOO));
        apacheConfig = new org.apache.dubbo.config.ApplicationConfig(FOO);
        DubboReflectUtils.setParameters(apacheConfig, Collections.singletonMap(FOO, BAR));
    }

    /**
     * 初始化
     */
    @BeforeClass
    public static void init() {
        Map<String, Object> map = new HashMap<>();
        map.put(CommonConstant.LOG_SETTING_FILE_KEY,
            DubboReflectUtilsTest.class.getResource("/logback-test.xml").getPath());
        LoggerFactory.init(map);
    }

    /**
     * 测试获取queryMap
     */
    @Test
    public void testGetQueryMap() {
        Map<String, String> queryMap = DubboReflectUtils.getQueryMap(new Entity());
        Assert.assertNotNull(queryMap);
        Assert.assertEquals(BAR, queryMap.get(FOO));
    }

    /**
     * 测试获取私有字段
     */
    @Test
    public void testGetFieldValue() {
        Map<String, String> queryMap = (Map<String, String>) ReflectUtils.getFieldValue(new Entity(), "queryMap")
            .orElse(null);
        Assert.assertNotNull(queryMap);
        Assert.assertEquals(BAR, queryMap.get(FOO));

        Assert.assertFalse(ReflectUtils.getFieldValue(new Entity(), "test").isPresent());
    }

    /**
     * 获取应用地址
     *
     * @see com.alibaba.dubbo.common.URL
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testGetAddress() {
        Assert.assertEquals("localhost:8080", DubboReflectUtils.getAddress(ALIBABA_URL));
        Assert.assertEquals("localhost:8081", DubboReflectUtils.getAddress(APACHE_URL));
    }

    /**
     * 获取dubbo应用名
     *
     * @see com.alibaba.dubbo.config.ApplicationConfig
     * @see org.apache.dubbo.config.ApplicationConfig
     */
    @Test
    public void testGetName() {
        Assert.assertEquals(BAR, DubboReflectUtils.getName(alibabaConfig));
        Assert.assertEquals(FOO, DubboReflectUtils.getName(apacheConfig));
    }

    /**
     * 获取参数
     *
     * @see com.alibaba.dubbo.common.URL
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testGetParameter() {
        Assert.assertEquals(BAR, DubboReflectUtils.getParameter(ALIBABA_URL, FOO, BAR));
        Assert.assertEquals(FOO, DubboReflectUtils.getParameter(APACHE_URL, BAR, FOO));

        Assert.assertEquals(FOO, DubboReflectUtils.getParameter(ALIBABA_URL, BAR));
        Assert.assertEquals(BAR, DubboReflectUtils.getParameter(APACHE_URL, FOO));
    }

    /**
     * 获取应用参数
     *
     * @see com.alibaba.dubbo.config.ApplicationConfig
     * @see org.apache.dubbo.config.ApplicationConfig
     */
    @Test
    public void testGetParameters() {
        Assert.assertEquals(FOO, DubboReflectUtils.getParameters(alibabaConfig).get(BAR));
        Assert.assertEquals(BAR, DubboReflectUtils.getParameters(APACHE_URL).get(FOO));
    }

    /**
     * 获取url
     *
     * @see com.alibaba.dubbo.rpc.Invoker
     * @see org.apache.dubbo.rpc.Invoker
     */
    @Test
    public void testGetUrl() {
        Assert.assertEquals(ALIBABA_URL, DubboReflectUtils.getUrl(new AlibabaInvoker<>()));
        Assert.assertEquals(APACHE_URL, DubboReflectUtils.getUrl(new ApacheInvoker<>()));
    }

    /**
     * 获取服务接口名
     *
     * @see com.alibaba.dubbo.common.URL
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testGetServiceInterface() {
        Assert.assertEquals("com.huawei.foo.BarTest", DubboReflectUtils.getServiceInterface(ALIBABA_URL));
        Assert.assertEquals("com.huawei.foo.FooTest", DubboReflectUtils.getServiceInterface(APACHE_URL));
    }

    /**
     * 获取dubbo请求方法名
     *
     * @see com.alibaba.dubbo.rpc.Invocation
     * @see org.apache.dubbo.rpc.Invocation
     */
    @Test
    public void testGetMethodName() {
        Assert.assertEquals("BarTest", DubboReflectUtils.getMethodName(new AlibabaInvocation()));
        Assert.assertEquals("FooTest", DubboReflectUtils.getMethodName(new ApacheInvocation()));
    }

    /**
     * 获取dubbo请求参数
     *
     * @see com.alibaba.dubbo.rpc.Invocation
     * @see org.apache.dubbo.rpc.Invocation
     */
    @Test
    public void testGetArguments() {
        Object[] alibabaArguments = DubboReflectUtils.getArguments(new AlibabaInvocation());
        Assert.assertEquals(2, alibabaArguments.length);
        Assert.assertEquals(BAR, alibabaArguments[0]);
        Assert.assertEquals(FOO, alibabaArguments[1]);

        Object[] apacheArguments = DubboReflectUtils.getArguments(new ApacheInvocation());
        Assert.assertEquals(2, alibabaArguments.length);
        Assert.assertEquals(FOO, apacheArguments[0]);
        Assert.assertEquals(BAR, apacheArguments[1]);
    }

    /**
     * 设置注册时的参数
     *
     * @see com.alibaba.dubbo.config.ApplicationConfig
     * @see org.apache.dubbo.config.ApplicationConfig
     */
    @Test
    public void testSetParameters() {
        // 由于注册时已经调用过ReflectUtils.setParameters方法，所以这里只要验证值就行
        Assert.assertEquals(FOO, alibabaConfig.getParameters().get(BAR));
        Assert.assertEquals(BAR, apacheConfig.getParameters().get(FOO));
    }

    /**
     * 获取权限检查类
     */
    @Test
    public void testGetAccessibleObject() throws NoSuchMethodException {
        Method method = Entity.class.getDeclaredMethod("getQueryMap");
        Assert.assertFalse(method.isAccessible());
        Assert.assertTrue(ReflectUtils.getAccessibleObject(method).isAccessible());
    }

    /**
     * 测试类
     *
     * @since 2022-03-18
     */
    public static class Entity {
        private final Map<String, String> queryMap = new HashMap<>();

        /**
         * 构造方法
         */
        public Entity() {
            queryMap.put(FOO, BAR);
        }

        private Map<String, String> getQueryMap() {
            return queryMap;
        }
    }

    /**
     * 测试类
     *
     * @since 2022-03-18
     */
    public static class AlibabaInvoker<T> implements Invoker<T> {
        @Override
        public Class<T> getInterface() {
            return null;
        }

        @Override
        public Result invoke(Invocation invocation) throws RpcException {
            return null;
        }

        @Override
        public URL getUrl() {
            return ALIBABA_URL;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void destroy() {
        }
    }

    /**
     * 测试类
     *
     * @since 2022-03-18
     */
    public static class ApacheInvoker<T> implements org.apache.dubbo.rpc.Invoker<T> {
        @Override
        public Class<T> getInterface() {
            return null;
        }

        @Override
        public org.apache.dubbo.rpc.Result invoke(org.apache.dubbo.rpc.Invocation invocation)
            throws org.apache.dubbo.rpc.RpcException {
            return null;
        }

        @Override
        public org.apache.dubbo.common.URL getUrl() {
            return APACHE_URL;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void destroy() {
        }
    }

    /**
     * 测试类
     *
     * @since 2022-03-18
     */
    public static class AlibabaInvocation implements Invocation {
        @Override
        public String getMethodName() {
            return "BarTest";
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return new Class[0];
        }

        @Override
        public Object[] getArguments() {
            return new Object[]{BAR, FOO};
        }

        @Override
        public Map<String, String> getAttachments() {
            return Collections.emptyMap();
        }

        @Override
        public String getAttachment(String str) {
            return "";
        }

        @Override
        public String getAttachment(String str, String str1) {
            return "";
        }

        @Override
        public Invoker<?> getInvoker() {
            return new AlibabaInvoker<>();
        }

        @Override
        public Object put(Object o1, Object o2) {
            return new Object();
        }

        @Override
        public Object get(Object obj) {
            return new Object();
        }

        @Override
        public Map<Object, Object> getAttributes() {
            return Collections.emptyMap();
        }
    }

    /**
     * 测试类
     *
     * @since 2022-03-18
     */
    public static class ApacheInvocation implements org.apache.dubbo.rpc.Invocation {
        @Override
        public String getTargetServiceUniqueName() {
            return "";
        }

        @Override
        public String getProtocolServiceKey() {
            return "";
        }

        @Override
        public String getMethodName() {
            return "FooTest";
        }

        @Override
        public String getServiceName() {
            return "";
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return new Class[0];
        }

        @Override
        public Object[] getArguments() {
            return new Object[]{FOO, BAR};
        }

        @Override
        public Map<String, String> getAttachments() {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, Object> getObjectAttachments() {
            return Collections.emptyMap();
        }

        @Override
        public void setAttachment(String key, String value) {
        }

        @Override
        public void setAttachment(String key, Object value) {
        }

        @Override
        public void setObjectAttachment(String key, Object value) {
        }

        @Override
        public void setAttachmentIfAbsent(String key, String value) {
        }

        @Override
        public void setAttachmentIfAbsent(String key, Object value) {
        }

        @Override
        public void setObjectAttachmentIfAbsent(String key, Object value) {
        }

        @Override
        public String getAttachment(String key) {
            return "";
        }

        @Override
        public String getAttachment(String key, String defaultValue) {
            return "";
        }

        @Override
        public Object getObjectAttachment(String key) {
            return new Object();
        }

        @Override
        public Object getObjectAttachment(String key, Object defaultValue) {
            return "";
        }

        @Override
        public org.apache.dubbo.rpc.Invoker<?> getInvoker() {
            return new ApacheInvoker<>();
        }

        @Override
        public Object put(Object key, Object value) {
            return new Object();
        }

        @Override
        public Object get(Object key) {
            return new Object();
        }

        @Override
        public Map<Object, Object> getAttributes() {
            return Collections.emptyMap();
        }
    }
}