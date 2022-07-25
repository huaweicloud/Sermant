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
import com.huawei.intergration.config.supprt.KieClient;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

/**
 * Nacos配置测试
 *
 * @author zhouss
 * @since 2022-07-14
 */
public class NacosConfigTest {
    @Rule
    public final TestRule nacosRunCondition = new NacosTestRule();

    private final RestTemplate restTemplate = new RestTemplate();

    private final String nacosUrl = "http://127.0.0.1:8848";

    private final KieClient kieClient = new KieClient(restTemplate);

    private final String serverUrl = "http://127.0.0.1:8989";

    private final String dataId = "nacos-dynamic-config.properties";

    private final String group = "DEFAULT_GROUP";

    private NacosConfigService configService;

    private final String key1 = "sermant.test";
    private final String keyA = "sermant.param1";
    private final String keyB = "sermant.param2";

    /**
     * 初始化nacos配置
     *
     * @throws Exception 运行报错
     */
    @Before
    public void publishNacosConfig() throws Exception {
        final Properties properties = new Properties();
        properties.put("serverAddr", nacosUrl);
        properties.put("enableRemoteSyncConfig", "false");
        configService = new NacosConfigService(properties);
        Assert.assertTrue(configService.publishConfig(dataId, group, "sermant.test=1\nsermant"
                        + ".param1=a\nsermant.param2=b",
                ConfigType.PROPERTIES.getType()));
    }

    private void checkNacos() throws NacosException, IOException {
        System.out.printf(Locale.ENGLISH, "dataId: %s, group: %s%n", dataId, group);
        String config = configService.getConfig(dataId, group, 10000L);
        if (config == null) {
            config = configService.getConfig(dataId, group, 10000L);
        }
        Assert.assertNotNull(config);
        final Properties properties = new Properties();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                config.getBytes(StandardCharsets.UTF_8));
        properties.load(byteArrayInputStream);
        byteArrayInputStream.close();
        final String result = get("/dynamic/config/value", String.class);
        Assert.assertEquals(result, properties.getProperty(key1));
        final String property = get("/dynamic/config/property", String.class);
        Assert.assertEquals(property, properties.getProperty(keyA) + "," + properties.getProperty(keyB));
    }

    /**
     * 测试启动屏蔽
     */
    @Test
    public void testBanNacosConfigCenter() {
        final Boolean isOpen = get("/dynamic/config/check", Boolean.class);
        if (isOpen) {
            return;
        }
        publishKieConfig();
        checkAgentConfig();
    }

    private boolean checkAgentConfig() {
        final String result = restTemplate.getForObject(serverUrl + "/dynamic/config/value", String.class);
        if ("1k".equals(result)) {
            final String property = restTemplate.getForObject(serverUrl + "/dynamic/config/property", String.class);
            return "ak,bk".equals(property);
        }
        return false;
    }

    /**
     * 测试动态关闭，即运行时屏蔽
     *
     * @throws Exception 运行出错
     */
    @Test
    public void testDynamicClose() throws Exception {
        final Boolean isOpen = get("/dynamic/config/check", Boolean.class);
        if (!isOpen) {
            return;
        }
        checkNacos();
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

    private <T> T get(String api, Class<T> responseClass) {
        return restTemplate.getForObject(serverUrl + api, responseClass);
    }
}
