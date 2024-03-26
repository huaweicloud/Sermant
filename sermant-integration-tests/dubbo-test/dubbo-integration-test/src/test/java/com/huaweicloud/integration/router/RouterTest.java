/*
 * Copyright (C) 2022-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.integration.router;

import com.huaweicloud.integration.support.KieClient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 区域路由测试
 *
 * @author provenceee
 * @since 2022-09-28
 */
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "router")
public class RouterTest {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private static final String BASE_URL = "http://127.0.0.1:28020/consumer/";

    private static final int TIMES = 30;

    private static final int WAIT_SECONDS = 30;

    private final String testTagRouterBaseUrl;

    private static final KieClient KIE_CLIENT = new KieClient(REST_TEMPLATE);

    private static final String SERVICE_KEY = "servicecomb.routeRule.dubbo-integration-provider";

    private static final String GLOBAL_KEY = "servicecomb.globalRouteRule";

    /**
     * 增加环境变量，控制dubbo3场景暂时不测试spring场景
     */
    private final boolean isExecuteSpringTest;

    /**
     * 构造方法
     */
    public RouterTest() throws InterruptedException {
        testTagRouterBaseUrl =
                "http://127.0.0.1:" + System.getProperty("controller.port", "28019") + "/controller/getMetadataBy";
        isExecuteSpringTest = Boolean.parseBoolean(System.getProperty("execute.spring.test", "true"));
        clearConfig();
    }

    /**
     * 测试根据请求信息路由，该测试用例会模拟停服务的情况，所以执行结果与用例顺序强相关
     */
    @Test
    public void testRouter() throws InterruptedException {
        if (!isExecuteSpringTest) {
            return;
        }
        HttpHeaders headers = new HttpHeaders();

        // 测试命中Test-Env:env-001的实例
        headers.add("Test-Env", "env-001");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                    .exchange(BASE_URL + "getMetadata?exit=false", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("Test-Env:env-001"));
        }

        // 测试没有命中Test-Env:env-005的实例时，切换至无Test-Env的实例
        headers.clear();
        headers.add("Test-Env", "env-005");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                    .exchange(BASE_URL + "getMetadata?exit=false", HttpMethod.GET, entity, String.class);
            Assertions.assertFalse(Objects.requireNonNull(exchange.getBody()).contains("Test-Env:"));
        }

        // 测试没有路由请求头时，优先切换至无路由标签的实例
        headers.clear();
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE
                    .exchange(BASE_URL + "getMetadata?exit=false", HttpMethod.GET, entity, String.class);
            Assertions.assertFalse(Objects.requireNonNull(exchange.getBody()).contains("Test-Env"));
        }

        // 停掉无路由标签的实例
        Assertions.assertThrows(Exception.class, () -> REST_TEMPLATE
                .exchange(BASE_URL + "getMetadata?exit=true", HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()),
                        String.class));

        // 等待无路由标签的实例下线
        headers.clear();
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < WAIT_SECONDS; i++) {
            try {
                exchange = REST_TEMPLATE
                        .exchange(BASE_URL + "getMetadata?exit=false", HttpMethod.GET, entity, String.class);
                if (Objects.requireNonNull(exchange.getBody()).contains("Test-Env")) {
                    // 下游实例已下线
                    break;
                }
            } catch (Exception ignored) {
                // 下游实例还未剔除，忽略
            }
            TimeUnit.SECONDS.sleep(1);
        }

        // 停掉标签为Test-Env1=env-002的实例
        headers.clear();
        headers.add("Test-Env1", "env-002");
        Assertions.assertThrows(Exception.class, () -> REST_TEMPLATE
                .exchange(BASE_URL + "getMetadata?exit=true", HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class));

        // 等待标签为Test-Env1=env-002的实例下线
        headers.clear();
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < WAIT_SECONDS; i++) {
            try {
                exchange = REST_TEMPLATE
                        .exchange(BASE_URL + "getMetadata?exit=false", HttpMethod.GET, entity, String.class);
                String body = Objects.requireNonNull(exchange.getBody());
                if (!body.contains("Test-Env1")) {
                    break;
                }
            } catch (Exception ignored) {
                // 下游实例还未剔除，忽略
            }
            TimeUnit.SECONDS.sleep(1);
        }

        // 测试没有命中Test-Env:env-005的实例时，切换至无Test-Env的实例，如果没有无Test-Env的实例，则返回空列表，即调用报错
        headers.clear();
        headers.add("Test-Env", "env-005");
        Assertions.assertThrows(Exception.class, () -> REST_TEMPLATE
                .exchange(BASE_URL + "getMetadata?exit=false", HttpMethod.GET,
                        new HttpEntity<>(null, new HttpHeaders(headers)),
                        String.class));
    }


    /**
     * 测试标签路由: 只下发服务粒度的flow匹配规则
     */
    @Test
    public void testRouterWithFlowMatchRule() throws InterruptedException {
        testFlowMatchRule(SERVICE_KEY);
        clearConfig();
    }

    /**
     * 测试标签路由: 只下发全局粒度的flow匹配规则
     */
    @Test
    public void testRouterWithGlobalFlowMatchRule() throws InterruptedException {
        testFlowMatchRule(GLOBAL_KEY);
        clearConfig();
    }

    /**
     * 测试标签路由: 同时下发服务粒度和全局粒度的flow匹配规则
     */
    @Test
    public void testRouterWithFlowMatchRuleAndGlobalRule() throws InterruptedException {
        // 先下发flow匹配的全局路由规则
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/flow\n"
                + "  description: flow-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        headers:\n"
                + "          id:\n"
                + "            exact: '1'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: yellow\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        headers:\n"
                + "          name:\n"
                + "            exact: 'bar'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 0.0.0\n"
                + "          weight: 100";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(GLOBAL_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        testFlowMatchRule(SERVICE_KEY);
        clearConfig();
    }

    /**
     * 测试标签路由: 只下发服务粒度的tag匹配规则
     */
    @Test
    public void testRouterWithTagMatchRule() throws InterruptedException {
        testTagMatchRule(SERVICE_KEY);
        clearConfig();
    }

    /**
     * 测试标签路由: 只下发全局粒度的tag匹配规则
     */
    @Test
    public void testRouterWithGlobalTagMatchRule() throws InterruptedException {
        testTagMatchRule(GLOBAL_KEY);
        clearConfig();
    }

    /**
     * 测试标签路由: 同时下发服务粒度和全局粒度的tag匹配规则
     */
    @Test
    public void testRouterWithTagMatchRuleAndGlobalRule() throws InterruptedException {
        // 先下发tag匹配的全局路由规则
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'red'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: gray\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'gray'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 1.0.1\n"
                + "          weight: 100";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(GLOBAL_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        testTagMatchRule(SERVICE_KEY);
        clearConfig();
    }

    /**
     * 测试标签路由: 同时下发flow匹配规则和tag匹配规则
     */
    @Test
    public void testRouterWithFlowAndTagRules() throws InterruptedException {
        testFlowAndTagMatchRule();
        clearConfig();
    }

    /**
     * 标签路由测试：同标签路由场景测试（只下发tag匹配规则）
     */
    @Test
    public void testRouterWithConsumerTagRule() throws InterruptedException {
        testConsumerTagRule();
        clearConfig();
    }

    /**
     * given：标签路由测试：同标签路由场景测试（规则含有TriggerThreshold的policy）
     * when：触发大于triggerThreshold
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */
    @Test
    public void testRouterWithTriggerThresholdPolicyRuleOne() throws InterruptedException {
        testTriggerThresholdPolicyAZRuleOne();
        clearConfig();
    }

    /**
     * given：标签路由测试：同标签路由场景测试（规则含有TriggerThreshold的policy）
     * when：触发小于triggerThreshold
     * then：非同AZ优先策略
     *
     * @throws InterruptedException
     */
    @Test
    public void testRouterWithTriggerThresholdPolicyRuleTwo() throws InterruptedException {
        testTriggerThresholdPolicyAZRuleTwo();
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：大于minAllInstances和大于triggerThreshold场景
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */
    @Test
    public void testRouterPolicyRuleOne() throws InterruptedException {
        testPolicyAZRuleOne();
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：大于minAllInstances和小于triggerThreshold场景
     * then：非同AZ优先策略
     *
     * @throws InterruptedException
     */
    @Test
    public void testRouterPolicyRuleTwo() throws InterruptedException {
        testPolicyAZRuleTwo();
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：小于minAllInstances和大于triggerThreshold场景
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */
    @Test
    public void testRouterPolicyRuleThree() throws InterruptedException {
        testPolicyAZRuleThree();
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：小于minAllInstances和小于triggerThreshold场景
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */
    @Test
    public void testRouterPolicyRuleFour() throws InterruptedException {
        testPolicyAZRuleFour();
        clearConfig();
    }

    /**
     * 测试flow匹配规则的标签路由功能
     */
    private void testFlowMatchRule(String configKey) throws InterruptedException {
        // 下发flow匹配的路由规则
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/flow\n"
                + "  description: flow-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        headers:\n"
                + "          id:\n"
                + "            exact: '1'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: gray\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        headers:\n"
                + "          name:\n"
                + "            exact: 'bar'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 1.0.1\n"
                + "          weight: 100";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(configKey, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group-test:gray的实例
        headers.add("id", "1");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("group-test:gray"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("group-test:gray"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("group-test:gray"));
            }
        }

        // 测试命中version:1.0.1的实例
        headers.clear();
        headers.add("name", "BAr");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("1.0.1"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("1.0.1"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("1.0.1"));
            }
        }

        // 测试没有命中version:1.0.1的实例
        headers.clear();
        headers.add("name", "foo");
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            String body = Objects.requireNonNull(exchange.getBody());
            Assertions.assertTrue(!body.contains("1.0.1") && !body.contains("group-test:gray"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                body = Objects.requireNonNull(exchange.getBody());
                Assertions.assertTrue(!body.contains("1.0.1") && !body.contains("group-test:gray"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                body = Objects.requireNonNull(exchange.getBody());
                Assertions.assertTrue(!body.contains("1.0.1") && !body.contains("group-test:gray"));
            }
        }

        // 测试没有命中version:1.0.1的实例
        headers.clear();
        entity = new HttpEntity<>(null, headers);
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            String body = Objects.requireNonNull(exchange.getBody());
            Assertions.assertTrue(!body.contains("1.0.1") && !body.contains("group-test:gray"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                body = Objects.requireNonNull(exchange.getBody());
                Assertions.assertTrue(!body.contains("1.0.1") && !body.contains("group-test:gray"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                body = Objects.requireNonNull(exchange.getBody());
                Assertions.assertTrue(!body.contains("1.0.1") && !body.contains("group-test:gray"));
            }
        }
    }

    /**
     * 测试flow匹配规则的标签路由功能
     */
    private void testTagMatchRule(String configKey) throws InterruptedException {
        // 下发tag匹配的路由规则
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'red'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: gray\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'gray'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 1.0.0\n"
                + "          weight: 100";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(configKey, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        // 测试命中version:1.0.0的实例(consumer自身group-test:gray)
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("1.0.0"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("1.0.0"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("1.0.0"));
            }
        }

        // 下发tag匹配的路由规则
        CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'gray'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: gray\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'red'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 1.0.0\n"
                + "          weight: 100";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(configKey, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        // 测试命中group-test:gray的实例(consumer自身group-test:gray)
        entity = new HttpEntity<>(null, new HttpHeaders());
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("group-test:gray"));
            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("group-test:gray"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("group-test:gray"));
            }
        }
    }

    /**
     * 测试同时存在flow和tag匹配规则的标签路由功能
     */
    private void testFlowAndTagMatchRule() throws InterruptedException {
        // 下发flow和tag匹配的路由规则
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/flow\n"
                + "  description: flow-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        headers:\n"
                + "          id:\n"
                + "            exact: '1'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: red\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        headers:\n"
                + "          name:\n"
                + "            exact: 'bar'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 1.0.1\n"
                + "          weight: 100\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'gray'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: gray\n"
                + "          weight: 100\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'red'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            version: 1.0.0\n"
                + "          weight: 100";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        // 测试命中标签为version:1.0.1和group-test:gray标签的实例
        HttpHeaders headers = new HttpHeaders();
        headers.add("name", "BAr");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            String body = Objects.requireNonNull(exchange.getBody());
            Assertions.assertTrue(body.contains("1.0.1") && body.contains("group-test:gray"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                body = Objects.requireNonNull(exchange.getBody());
                Assertions.assertTrue(body.contains("1.0.1") && body.contains("group-test:gray"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                body = Objects.requireNonNull(exchange.getBody());
                Assertions.assertTrue(body.contains("1.0.1") && body.contains("group-test:gray"));
            }
        }
    }

    /**
     * 测试tag匹配规则的标签路由功能
     */
    private void testConsumerTagRule() throws InterruptedException {
        // 下发同标签路由的路由规则
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-rule-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          group-test:\n"
                + "            exact: 'gray'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            group-test: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group-test:CONSUMER_TAG的实例(consumer自身group-test:gray)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("gray"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("gray"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("gray"));
            }
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold的policy
     * 触发大于triggerThreshold
     */
    private void testTriggerThresholdPolicyAZRuleOne() throws InterruptedException {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 40\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group:CONSUMER_TAG的实例(consumer自身zone:az1)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));
            }
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold的policy
     * 触发小于triggerThreshold
     */
    private void testTriggerThresholdPolicyAZRuleTwo() throws InterruptedException {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 90\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group:CONSUMER_TAG的实例(consumer自身group:gray)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        int dubboAZ1 = 0, dubboAZ2 = 0;
        int feignAZ1 = 0, feignAZ2 = 0;
        int restAZ1 = 0, restAZ2 = 0;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            if (Objects.requireNonNull(exchange.getBody()).contains("az1")) {
                dubboAZ1++;
            } else if (Objects.requireNonNull(exchange.getBody()).contains("az2")) {
                dubboAZ2++;
            }

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                if (Objects.requireNonNull(exchange.getBody()).contains("az1")) {
                    feignAZ1++;
                } else if (Objects.requireNonNull(exchange.getBody()).contains("az2")) {
                    feignAZ2++;
                }
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                if (Objects.requireNonNull(exchange.getBody()).contains("az1")) {
                    restAZ1++;
                } else if (Objects.requireNonNull(exchange.getBody()).contains("az2")) {
                    restAZ2++;
                }
            }
        }
        Assertions.assertNotEquals(0, dubboAZ1);
        Assertions.assertNotEquals(0, dubboAZ2);
        if (isExecuteSpringTest) {
            Assertions.assertNotEquals(0, feignAZ1);
            Assertions.assertNotEquals(0, feignAZ2);
            Assertions.assertNotEquals(0, restAZ1);
            Assertions.assertNotEquals(0, restAZ2);
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 大于minAllInstances和大于triggerThreshold场景
     * 同AZ优先策略
     *
     * @throws InterruptedException
     */

    private void testPolicyAZRuleOne() throws InterruptedException {
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 40\n"
                + "          minAllInstances: 2\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group:CONSUMER_TAG的实例(consumer自身zone:az1)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));
            }
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 大于minAllInstances和小于triggerThreshold场景
     * 非同AZ优先策略
     *
     * @throws InterruptedException
     */
    private void testPolicyAZRuleTwo() throws InterruptedException {
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 90\n"
                + "          minAllInstances: 2\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group:CONSUMER_TAG的实例(consumer自身zone:az1)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        int dubboAZ1 = 0, dubboAZ2 = 0;
        int feignAZ1 = 0, feignAZ2 = 0;
        int restAZ1 = 0, restAZ2 = 0;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            if (Objects.requireNonNull(exchange.getBody()).contains("az1")) {
                dubboAZ1++;
            } else if (Objects.requireNonNull(exchange.getBody()).contains("az2")) {
                dubboAZ2++;
            }

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                if (Objects.requireNonNull(exchange.getBody()).contains("az1")) {
                    feignAZ1++;
                } else if (Objects.requireNonNull(exchange.getBody()).contains("az2")) {
                    feignAZ2++;
                }
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                if (Objects.requireNonNull(exchange.getBody()).contains("az1")) {
                    restAZ1++;
                } else if (Objects.requireNonNull(exchange.getBody()).contains("az2")) {
                    restAZ2++;
                }
            }
        }
        Assertions.assertNotEquals(0, dubboAZ1);
        Assertions.assertNotEquals(0, dubboAZ2);
        if (isExecuteSpringTest) {
            Assertions.assertNotEquals(0, feignAZ1);
            Assertions.assertNotEquals(0, feignAZ2);
            Assertions.assertNotEquals(0, restAZ1);
            Assertions.assertNotEquals(0, restAZ2);
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 小于minAllInstances和大于triggerThreshold场景
     * 同AZ优先策略
     *
     * @throws InterruptedException
     */

    private void testPolicyAZRuleThree() throws InterruptedException {
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 40\n"
                + "          minAllInstances: 10\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group:CONSUMER_TAG的实例(consumer自身zone:az1)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));
            }
        }
    }


    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 小于minAllInstances和小于triggerThreshold场景
     * 同AZ优先策略
     *
     * @throws InterruptedException
     */

    private void testPolicyAZRuleFour() throws InterruptedException {
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 90\n"
                + "          minAllInstances: 10\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";

        Assertions.assertTrue(KIE_CLIENT.publishConfig(SERVICE_KEY, CONTENT));
        TimeUnit.SECONDS.sleep(3);

        HttpHeaders headers = new HttpHeaders();

        // 测试命中group:CONSUMER_TAG的实例(consumer自身zone:az1)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        for (int i = 0; i < TIMES; i++) {
            exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Dubbo", HttpMethod.GET, entity, String.class);
            Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

            if (isExecuteSpringTest) {
                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Feign", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));

                exchange = REST_TEMPLATE.exchange(testTagRouterBaseUrl + "Rest", HttpMethod.GET, entity, String.class);
                Assertions.assertTrue(Objects.requireNonNull(exchange.getBody()).contains("az1"));
            }
        }
    }

    private void clearConfig() throws InterruptedException {
        KIE_CLIENT.deleteKey(SERVICE_KEY);
        KIE_CLIENT.deleteKey(GLOBAL_KEY);
        TimeUnit.SECONDS.sleep(3);
    }
}