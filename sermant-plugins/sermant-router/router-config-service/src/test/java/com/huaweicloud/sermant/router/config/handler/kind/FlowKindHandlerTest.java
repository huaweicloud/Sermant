/*
 *
 *  * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.huaweicloud.sermant.router.config.handler.kind;

import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试流量配置处理器
 *
 * @author provenceee
 * @since 2024-01-11
 */
public class FlowKindHandlerTest extends AbstractKindHandlerTest {
    private final AbstractKindHandler handler;

    private final String content;

    private final String cacheName;

    public FlowKindHandlerTest() {
        this.handler = new FlowKindHandler();
        this.content = "servicecomb.globalRouteRule:\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        attachments:\n"
                + "          id:\n"
                + "            exact: '1'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - weight: 20\n"
                + "          tags:\n"
                + "            version: 1.0.0\n"
                + "        - weight: 80\n"
                + "          tags:\n"
                + "            version: 1.0.1\n"
                + "    - precedence: 1\n"
                + "      route:\n"
                + "        - weight: 20\n"
                + "          tags:\n"
                + "            group: red\n"
                + "        - weight: 80\n"
                + "          tags:\n"
                + "            group: green";
        this.cacheName = "flowKindHandlerTest";
    }

    /**
     * 测试shouldHandle方法
     */
    @Test
    public void testShouldHandle() {
        Assert.assertFalse(handler.shouldHandle(RouterConstant.GLOBAL_ROUTER_KEY));
        Assert.assertFalse(handler.shouldHandle(RouterConstant.GLOBAL_ROUTER_KEY));
    }

    /**
     * 测试shouldHandle方法
     */
    @Test
    public void testHandle() {
        // 初始化
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        Assert.assertTrue(CollectionUtils.isEmpty(configuration.getGlobalRule().get(RouterConstant.FLOW_MATCH_KIND)));

        // 测试存在配置
        DynamicConfigEvent event = new DynamicConfigEvent(RouterConstant.GLOBAL_ROUTER_KEY, "test", content,
                DynamicConfigEventType.CREATE);
        handler.handle(event, cacheName);
        Assert.assertFalse(CollectionUtils.isEmpty(configuration.getGlobalRule().get(RouterConstant.FLOW_MATCH_KIND)));

        // 测试删除配置
        event = new DynamicConfigEvent(RouterConstant.GLOBAL_ROUTER_KEY, "test", content,
                DynamicConfigEventType.DELETE);
        handler.handle(event, cacheName);
        Assert.assertTrue(CollectionUtils.isEmpty(configuration.getGlobalRule().get(RouterConstant.FLOW_MATCH_KIND)));
    }
}
