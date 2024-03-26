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

package com.huaweicloud.sermant.router.dubbo.mapper;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.MetadataInfo;
import org.apache.dubbo.registry.client.DefaultServiceInstance;
import org.apache.dubbo.registry.client.InstanceAddressURL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * dubbo3.x instance注册类型时元数据处理测试
 *
 * @author chengyouling
 * @since 2024-03-18
 */
public class Dubbo3InstanceMapperTest {
    private static final URL APACHE_URL = URL
            .valueOf("dubbo://localhost:8081/com.demo.foo.FooTest?foo=bar&version=0.0.1");

    private final InstanceAddressURL instanceAddressURL;

    /**
     * 构造方法
     */
    public Dubbo3InstanceMapperTest() {
        RpcContext.getServiceContext().setConsumerUrl(APACHE_URL);
        DefaultServiceInstance instance = new DefaultServiceInstance();
        instance.setHost("127.0.0.1");
        instance.setPort(8090);
        Map<String, String> meta = new HashMap<>();
        meta.put("az", "az1");
        meta.put("region", "region1");
        instance.setMetadata(meta);
        Map<String, MetadataInfo.ServiceInfo > services = new HashMap<>();
        MetadataInfo.ServiceInfo serviceInfo = new MetadataInfo.ServiceInfo();
        Map<String, String> params = new HashMap<>();
        params.put("az2", "az2");
        params.put("region2", "region2");
        serviceInfo.setParams(params);
        services.put("com.demo.foo.FooTest:0.0.1:dubbo", serviceInfo);
        MetadataInfo metadataInfo = new MetadataInfo("app", "0.0.1", services);
        instanceAddressURL = new InstanceAddressURL(instance, metadataInfo);
    }

    /**
     * 测试获取metadata
     */
    @Test
    public void testApply() {
        Invoker invoker = new Invoker() {
            @Override
            public Class getInterface() {
                return null;
            }

            @Override
            public Result invoke(Invocation invocation) throws RpcException {
                return null;
            }

            @Override
            public URL getUrl() {
                return instanceAddressURL;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public void destroy() {

            }
        };
        Dubbo3InstanceMapper mapper = new Dubbo3InstanceMapper();
        Map<String, String> queryMap = mapper.apply(invoker);
        Assert.assertEquals(4, queryMap.size());
    }
}