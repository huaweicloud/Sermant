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
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.service.BaseService;
import com.huaweicloud.sermant.core.service.ServiceManager;
import com.huaweicloud.sermant.router.dubbo.interceptor.RegistrationInterceptor;
import com.huaweicloud.sermant.router.dubbo.service.RegistrationService;

import org.apache.servicecomb.service.center.client.model.Microservice;
import org.apache.servicecomb.service.center.client.model.MicroserviceInstance;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试RegistrationInterceptor
 *
 * @author provenceee
 * @since 2022-03-21
 */
public class RegistrationInterceptorTest {
    private final Map<String, String> map;

    private final List<MicroserviceInstance> instances;

    private String version;

    private final RegistrationInterceptor interceptor;

    /**
     * 构造方法
     */
    public RegistrationInterceptorTest() throws NoSuchFieldException, IllegalAccessException {
        map = new HashMap<>();
        instances = new ArrayList<>();
        MicroserviceInstance instance = new MicroserviceInstance();
        instance.setEndpoints(Collections.singletonList("dubbo://127.0.0.1:8080"));
        instance.setVersion("0.0.2");
        instances.add(instance);
        Field field = ServiceManager.class.getDeclaredField("SERVICES");
        field.setAccessible(true);
        Map<String, BaseService> serviceMap = (Map<String, BaseService>) field.get(null);
        serviceMap.put(RegistrationService.class.getCanonicalName(), new RegistrationService() {
            @Override
            public void setRegisterVersionCache(String addr, String registerVersion) {
                map.put(addr, registerVersion);
            }

            @Override
            public void setRegisterVersion(String registerVersion) {
                version = registerVersion;
            }
        });
        interceptor = new RegistrationInterceptor();
    }

    /**
     * 初始化
     */
    @BeforeClass
    public static void init() {
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put(CommonConstant.LOG_SETTING_FILE_KEY,
            RegistrationInterceptorTest.class.getResource("/logback-test.xml").getPath());
        LoggerFactory.init(argsMap);
    }

    /**
     * 测试RegistrationInterceptor
     */
    @Test
    public void test() {
        Entity entity = new Entity();

        // 数组长度无效
        ExecuteContext context = ExecuteContext.forMemberMethod(entity, null, new Object[1], null, null);
        interceptor.before(context);
        Assert.assertNull(version);

        // arguments[2]为null
        context = ExecuteContext.forMemberMethod(entity, null, new Object[3], null, null);
        interceptor.before(context);
        Assert.assertNull(version);

        // arguments[2]不为list
        Object[] arguments = new Object[3];
        arguments[2] = "bar";
        context = ExecuteContext.forMemberMethod(entity, null, arguments, null, null);
        interceptor.before(context);
        Assert.assertNull(version);

        // obj没有microservice字段
        arguments[2] = instances;
        context = ExecuteContext.forMemberMethod(new Object(), null, arguments, null, null);
        interceptor.before(context);
        Assert.assertNull(version);

        // microservice为null
        arguments[2] = instances;
        context = ExecuteContext.forMemberMethod(entity, null, arguments, null, null);
        interceptor.before(context);
        Assert.assertNull(version);

        // 正常情况
        Microservice microservice = new Microservice("bar");
        microservice.setVersion("0.0.1");
        entity.setMicroservice(microservice);
        interceptor.before(context);
        Assert.assertEquals("0.0.1", version);
        Assert.assertEquals("0.0.2", map.get("127.0.0.1:8080"));
    }

    /**
     * 测试类
     *
     * @since 2022-03-21
     */
    public static class Entity {
        private Microservice microservice;

        public Microservice getMicroservice() {
            return microservice;
        }

        public void setMicroservice(Microservice microservice) {
            this.microservice = microservice;
        }
    }
}