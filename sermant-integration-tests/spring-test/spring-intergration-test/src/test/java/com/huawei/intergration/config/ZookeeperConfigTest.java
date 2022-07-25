/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.intergration.config;

import com.huawei.intergration.config.rule.NacosTestRule;
import com.huawei.intergration.config.rule.ZkTestRule;
import com.huawei.intergration.config.supprt.KieClient;
import com.huawei.intergration.config.supprt.ZkClient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * zookeeper动态配置生效测试
 *
 * @author zhouss
 * @since 2022-07-15
 */
public class ZookeeperConfigTest {
    @Rule
    public final TestRule nacosRunCondition = new ZkTestRule();

    private final RestTemplate restTemplate = new RestTemplate();

    private final KieClient kieClient = new KieClient(restTemplate);

    private final String serverUrl = "http://127.0.0.1:8989";

    private final String key1 = "/sermant/test";
    private final String keyA = "/sermant/param1";
    private final String keyB = "/sermant/param2";

    private ZkClient zkClient;


    /**
     * 发布zk配置
     */
    @Before
    public void publishZkConfig() {
        zkClient = new ZkClient(null);
        Assert.assertTrue(zkClient.publishConfig(key1, "1"));
        Assert.assertTrue(zkClient.publishConfig(keyA, "a"));
        Assert.assertTrue(zkClient.publishConfig(keyB, "b"));
    }

    private void checkZkConfig() {
        final Optional<String> config1 = zkClient.getConfig(key1);
        Assert.assertTrue(config1.isPresent());
        Assert.assertEquals(config1.get(), "1");
        final Optional<String> config2 = zkClient.getConfig(keyA);
        Assert.assertTrue(config2.isPresent());
        Assert.assertEquals(config2.get(), "a");
        final Optional<String> config3 = zkClient.getConfig(keyB);
        Assert.assertTrue(config3.isPresent());
        Assert.assertEquals(config3.get(), "b");
        final String result = get("/dynamic/config/value", String.class);
        Assert.assertEquals(result, config1.get());
        final String property = get("/dynamic/config/property", String.class);
        Assert.assertEquals(property, config2.get() + "," + config3.get());
    }

    /**
     * 测试启动屏蔽
     */
    @Test
    public void testBanZkConfigCenter() {
        final Boolean isOpen = get("/dynamic/config/check", Boolean.class);
        if (!isOpen) {
            return;
        }
        publishKieConfig();
        Assert.assertTrue(checkAgentConfig());
    }

    /**
     * 测试动态配置
     *
     * @throws Exception 发布配置报错
     */
    @Test
    public void testDynamicClose() throws Exception {
        final Boolean isOpen = get("/dynamic/config/check", Boolean.class);
        if (!isOpen) {
            return;
        }
        checkZkConfig();
        publishKieConfig();
        // 发布动态关闭开关
        kieClient.publishConfig("closeOriginConfigCenter", "sermant.origin.config.needClose: true");
        // 睡眠等待刷新， 由于LocalCse无实时通知能力，因此需要等待22S（长连接时间）
        Thread.sleep(22 * 1000);
        Assert.assertTrue(checkAgentConfig());
    }

    private void publishKieConfig() {
        // 测试当前的值是否取与KIE配置中心而不是配置中心
        kieClient.publishConfig("testConfig", "sermant.test: 1k\nsermant"
                + ".param1: ak\nsermant.param2: bk");
    }

    private boolean checkAgentConfig() {
        final String result = restTemplate.getForObject(serverUrl + "/dynamic/config/value", String.class);
        if ("1k".equals(result)) {
            final String property = restTemplate.getForObject(serverUrl + "/dynamic/config/property", String.class);
            return "ak,bk".equals(property);
        }
        return false;
    }

    private <T> T get(String api, Class<T> responseClass) {
        return restTemplate.getForObject(serverUrl + api, responseClass);
    }

    @After
    public void close() {
        zkClient.close();
    }
}
