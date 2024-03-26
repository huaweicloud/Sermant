/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.integration.lane;

import com.alibaba.fastjson.JSONObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 泳道测试
 *
 * @author provenceee
 * @since 2023-03-03
 */
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "lane")
public class LaneTest {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private static final String PROVIDER_NAME = "dubbo-integration-provider";

    private static final String CONSUMER_NAME = "dubbo-integration-consumer";

    private static final String VERSION_KEY = "version";

    private static final int TIMES = 30;

    private final String baseUrl;

    /**
     * 增加环境变量，控制dubbo3场景暂时不测试spring场景
     */
    private final boolean isExecuteSpringTest;

    /**
     * 构造方法
     */
    public LaneTest() {
        baseUrl = "http://127.0.0.1:" + System.getProperty("controller.port", "28019") + "/controller/getLaneBy";
        isExecuteSpringTest = Boolean.parseBoolean(System.getProperty("execute.spring.test", "true"));
    }

    @Test
    public void testDubbo() {
        if (isExecuteSpringTest){
            testBySpring("Dubbo");
        }
        testByDubbo("Dubbo");

        // 正常染色（测试dubbo取值策略）
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + "Dubbo?id=100&name=FOo&laneId=10&arrName=foo123&listId=100"
                    + "&mapName=bar", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertEquals("gray3", providerMsg.getString("x-sermant-flag3"));
            Assertions.assertEquals("gray4", providerMsg.getString("x-sermant-flag4"));
            Assertions.assertEquals("1.0.1", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.0", consumerMsg.getString(VERSION_KEY));
        }

        // 测试已传入泳道标记（测试dubbo取值策略）
        headers.clear();
        headers.add("x-sermant-flag1", "gray1");
        headers.add("x-sermant-flag3", "gray13");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + "Dubbo?id=100&name=FOo&laneId=10&arrName=foo123&listId=100"
                    + "&mapName=bar", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertEquals("gray1", providerMsg.getString("x-sermant-flag1"));
            Assertions.assertEquals("gray13", providerMsg.getString("x-sermant-flag3"));
            Assertions.assertEquals("gray4", providerMsg.getString("x-sermant-flag4"));
            Assertions.assertEquals("1.0.1", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.1", consumerMsg.getString(VERSION_KEY));
        }

        // 测试不满足染色条件（测试dubbo取值策略）
        headers.clear();
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + "Dubbo?id=100&name=FOO&laneId=10&arrName=foo123&listId=100"
                    + "&mapName=bar", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertFalse(providerMsg.containsKey("x-sermant-flag3"));
            Assertions.assertFalse(providerMsg.containsKey("x-sermant-flag3"));
            Assertions.assertEquals("1.0.0", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.0", consumerMsg.getString(VERSION_KEY));
        }
    }

    @Test
    public void testFeign() {
        if (isExecuteSpringTest){
            testBySpring("Feign");
            testByDubbo("Feign");
        }
    }

    @Test
    public void testRest() {
        if (isExecuteSpringTest){
            testBySpring("Rest");
            testByDubbo("Rest");
        }
    }

    /**
     * 染色入口在spring
     *
     * @param path 路径
     */
    private void testBySpring(String path) {
        // 正常染色
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-user-id", "101");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + path + "?name=BaR&id=9&enabled=true", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertEquals("gray1", providerMsg.getString("x-sermant-flag1"));
            Assertions.assertEquals("gray2", providerMsg.getString("x-sermant-flag2"));
            Assertions.assertEquals("1.0.0", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.1", consumerMsg.getString(VERSION_KEY));
        }

        // 测试已传入泳道标记
        headers.clear();
        headers.add("x-user-id", "101");
        headers.add("x-sermant-flag1", "gray11");
        headers.add("x-sermant-flag3", "gray3");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + path + "?name=BaR&id=9&enabled=true", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertEquals("gray11", providerMsg.getString("x-sermant-flag1"));
            Assertions.assertEquals("gray2", providerMsg.getString("x-sermant-flag2"));
            Assertions.assertEquals("gray3", providerMsg.getString("x-sermant-flag3"));
            Assertions.assertEquals("1.0.0", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.0", consumerMsg.getString(VERSION_KEY));
        }

        // 测试不满足染色条件
        headers.clear();
        headers.add("x-user-id", "100");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + path + "?name=BaR&id=9&enabled=true", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertFalse(providerMsg.containsKey("x-sermant-flag1"));
            Assertions.assertFalse(providerMsg.containsKey("x-sermant-flag2"));
            Assertions.assertEquals("1.0.0", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.0", consumerMsg.getString(VERSION_KEY));
        }
    }

    /**
     * 染色入口在Dubbo
     *
     * @param path 路径
     */
    private void testByDubbo(String path) {
        // 正常染色
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + path + "?name=bar&id=100&enabled=true", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertEquals("gray5", providerMsg.getString("x-sermant-flag5"));
            Assertions.assertEquals("gray6", providerMsg.getString("x-sermant-flag6"));
            Assertions.assertEquals("1.0.1", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.0", consumerMsg.getString(VERSION_KEY));
        }

        // 测试已传入泳道标记
        headers.clear();
        headers.add("x-sermant-flag1", "gray1");
        headers.add("x-sermant-flag5", "gray15");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + path + "?name=bar&id=100&enabled=true", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertEquals("gray1", providerMsg.getString("x-sermant-flag1"));
            Assertions.assertEquals("gray15", providerMsg.getString("x-sermant-flag5"));
            Assertions.assertEquals("gray6", providerMsg.getString("x-sermant-flag6"));
            Assertions.assertEquals("1.0.0", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.1", consumerMsg.getString(VERSION_KEY));
        }

        // 测试不满足染色条件
        headers.clear();
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                .exchange(baseUrl + path + "?name=bar&id=99&enabled=true", HttpMethod.GET, entity, String.class);
            JSONObject json = JSONObject.parseObject(exchange.getBody());
            JSONObject providerMsg = json.getJSONObject(PROVIDER_NAME);
            JSONObject consumerMsg = json.getJSONObject(CONSUMER_NAME);
            Assertions.assertFalse(providerMsg.containsKey("x-sermant-flag5"));
            Assertions.assertFalse(providerMsg.containsKey("x-sermant-flag6"));
            Assertions.assertEquals("1.0.0", providerMsg.getString(VERSION_KEY));
            Assertions.assertEquals("1.0.0", consumerMsg.getString(VERSION_KEY));
        }
    }
}