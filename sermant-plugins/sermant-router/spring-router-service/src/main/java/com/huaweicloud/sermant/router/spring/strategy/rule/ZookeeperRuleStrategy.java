/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.spring.strategy.rule;

import com.huaweicloud.sermant.router.common.utils.ReflectUtils;
import com.huaweicloud.sermant.router.config.strategy.AbstractRuleStrategy;
import com.huaweicloud.sermant.router.spring.strategy.instance.MismatchInstanceStrategy;
import com.huaweicloud.sermant.router.spring.strategy.instance.TargetInstanceStrategy;
import com.huaweicloud.sermant.router.spring.utils.SpringReflectUtils;

import java.util.Collections;
import java.util.List;

/**
 * 流量匹配
 *
 * @author provenceee
 * @since 2021-10-14
 */
public class ZookeeperRuleStrategy extends AbstractRuleStrategy<Object> {
    /**
     * 构造方法
     */
    public ZookeeperRuleStrategy() {
        super(new TargetInstanceStrategy<>(), new MismatchInstanceStrategy<>(),
            obj -> {
                Object instanceInfo = ReflectUtils.invokeWithNoneParameter(obj, "getInstance");
                Object payload = ReflectUtils.invokeWithNoneParameter(instanceInfo, "getPayload");
                return SpringReflectUtils.getMetadata(payload);
            });
    }

    @Override
    public List<String> getName() {
        return Collections.singletonList("org.springframework.cloud.zookeeper.discovery.ZookeeperServer");
    }
}