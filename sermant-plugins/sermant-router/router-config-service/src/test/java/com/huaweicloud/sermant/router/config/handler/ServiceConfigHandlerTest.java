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

package com.huaweicloud.sermant.router.config.handler;

import com.huaweicloud.sermant.router.common.constants.RouterConstant;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试ServiceConfigHandler
 *
 * @author provenceee
 * @since 2022-10-10
 */
public class ServiceConfigHandlerTest {
    private final AbstractConfigHandler handler;

    public ServiceConfigHandlerTest() {
        this.handler = new ServiceConfigHandler();
    }

    /**
     * 测试shouldHandle方法
     */
    @Test
    public void testShouldHandle() {
        Assert.assertTrue(handler.shouldHandle("servicecomb.routeRule.foo", RouterConstant.FLOW_MATCH_KIND));
        Assert.assertFalse(handler.shouldHandle("servicecomb.routeRule", RouterConstant.FLOW_MATCH_KIND));
    }
}