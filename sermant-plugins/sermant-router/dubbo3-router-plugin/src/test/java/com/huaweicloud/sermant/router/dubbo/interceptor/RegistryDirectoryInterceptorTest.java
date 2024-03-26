/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.dubbo.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.router.common.cache.DubboCache;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * dubbo3.x all/interface注册类型时设置应用名
 *
 * @author chengyouling
 * @since 2024-03-18
 */
public class RegistryDirectoryInterceptorTest {
    private static final String SERVICE_NAME = "test-service";

    private static final URL APACHE_URL = URL.valueOf(
            "dubbo://localhost:8081/com.demo.foo.FooTest?foo=bar&version=0.0.1&application=" + SERVICE_NAME);


    private static final String SERVICE_INTERFACE = "com.demo.foo.FooTest";

    private final RegistryDirectoryInterceptor interceptor;

    /**
     * 构造方法
     */
    public RegistryDirectoryInterceptorTest() {
        RpcContext.getServiceContext().setConsumerUrl(APACHE_URL);
        interceptor = new RegistryDirectoryInterceptor();
    }

    /**
     * 测试设置应用名
     */
    @Test
    public void testBefore() {
        Object[] arguments = new Object[1];
        arguments[0] = APACHE_URL;
        ExecuteContext context = ExecuteContext.forMemberMethod(new Object(), null,
                arguments, null, null);
        interceptor.before(context);
        Assert.assertEquals(SERVICE_NAME, DubboCache.INSTANCE.getApplication(SERVICE_INTERFACE));
    }
}