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
 * 测试标签配置处理器
 *
 * @author provenceee
 * @since 2024-01-11
 */
public class TagKindHandlerTest extends AbstractKindHandlerTest {
    private final AbstractKindHandler handler;

    private final String content;

    private final String cacheName;

    public TagKindHandlerTest() {
        this.handler = new TagKindHandler();
        this.content = "servicecomb.tagRule.foo:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'hangzhou'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n"
                + "    - precedence: 2\n"
                + "      match:\n"
                + "        tags:\n"
                + "          version:\n"
                + "            exact: '1.0.0'\n"
                + "            caseInsensitive: false\n"
                + "      route:\n"
                + "        - weight: 20\n"
                + "          tags:\n"
                + "            group: red\n"
                + "        - weight: 80\n"
                + "          tags:\n"
                + "            group: green";
        this.cacheName = "TagKindHandlerTest";
    }

    /**
     * 测试shouldHandle方法
     */
    @Test
    public void testShouldHandle() {
        Assert.assertFalse(handler.shouldHandle(RouterConstant.TAG_KEY_PREFIX + ".foo"));
        Assert.assertFalse(
                handler.shouldHandle(RouterConstant.TAG_KEY_PREFIX + ".foo"));
    }

    /**
     * 测试shouldHandle方法
     */
    @Test
    public void testHandle() {
        // 初始化
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        Assert.assertTrue(CollectionUtils.isEmpty(configuration.getRouteRule().get(RouterConstant.TAG_MATCH_KIND)));

        // 测试存在配置
        DynamicConfigEvent event = new DynamicConfigEvent(RouterConstant.TAG_KEY_PREFIX + ".foo", "test", content,
                DynamicConfigEventType.CREATE);
        handler.handle(event, cacheName);
        Assert.assertFalse(CollectionUtils.isEmpty(configuration.getRouteRule().get(RouterConstant.TAG_MATCH_KIND)));

        // 测试删除配置
        event = new DynamicConfigEvent(RouterConstant.TAG_KEY_PREFIX + ".foo", "test", content,
                DynamicConfigEventType.DELETE);
        handler.handle(event, cacheName);
        Assert.assertTrue(CollectionUtils.isEmpty(configuration.getRouteRule().get(RouterConstant.TAG_MATCH_KIND)));
    }
}
