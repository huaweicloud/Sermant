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

package com.huawei.flowcontrol.inject.retry;

import com.huawei.flowcontrol.common.config.FlowControlConfig;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.plugin.inject.ClassInjectDefine;
import com.huaweicloud.sermant.core.utils.ClassUtils;

/**
 * 重试注入
 *
 * @author zhouss
 * @since 2022-07-23
 */
public class RetryInjectDefine implements ClassInjectDefine {
    @Override
    public String injectClassName() {
        return "com.huawei.flowcontrol.inject.retry.SpringRetryConfiguration";
    }

    @Override
    public String factoryName() {
        return ENABLE_AUTO_CONFIGURATION_FACTORY_NAME;
    }

    @Override
    public Plugin plugin() {
        return Plugin.FLOW_CONTROL_PLUGIN;
    }

    @Override
    public ClassInjectDefine[] requiredDefines() {
        return new ClassInjectDefine[]{
                this.build("com.huawei.flowcontrol.inject.retry.RetryClientHttpResponse", "", this::isLoadedClass),
                this.build("com.huawei.flowcontrol.inject.retry.RetryableRestTemplate", "", this::isLoadedClass)
        };
    }

    @Override
    public boolean canInject() {
        return PluginConfigManager.getPluginConfig(FlowControlConfig.class).isEnableRetry();
    }

    private boolean isLoadedClass() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return ClassUtils.loadClass("org.springframework.web.client.RestTemplate", contextClassLoader).isPresent();
    }
}
