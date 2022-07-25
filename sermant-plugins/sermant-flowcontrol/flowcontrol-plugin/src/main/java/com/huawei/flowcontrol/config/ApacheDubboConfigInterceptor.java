/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.config;

import com.huawei.flowcontrol.common.adapte.cse.constants.CseConstants;
import com.huawei.flowcontrol.common.adapte.cse.entity.FlowControlServiceMeta;
import com.huawei.flowcontrol.common.config.FlowControlConfig;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import org.apache.dubbo.common.utils.ConfigUtils;

/**
 * apache dubbo配置拦截
 *
 * @author zhouss
 * @since 2022-01-28
 */
public class ApacheDubboConfigInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        final FlowControlConfig pluginConfig = PluginConfigManager.getPluginConfig(FlowControlConfig.class);
        FlowControlServiceMeta.getInstance().setDubboService(true);
        if (!pluginConfig.isUseCseRule() || !pluginConfig.isBaseSdk()) {
            return context;
        }
        FlowControlServiceMeta.getInstance().setVersion(ConfigUtils.getProperty(CseConstants.KEY_DUBBO_VERSION,
            CseConstants.DEFAULT_DUBBO_VERSION));
        FlowControlServiceMeta.getInstance().setProject(ConfigUtils.getProperty(CseConstants.KEY_DUBBO_KIE_PROJECT,
            CseConstants.DEFAULT_PROJECT));
        FlowControlServiceMeta.getInstance().setServiceName(ConfigUtils.getProperty(CseConstants.KEY_DUBBO_SERVICE_NAME,
            CseConstants.DEFAULT_DUBBO_SERVICE_NAME));
        FlowControlServiceMeta.getInstance().setEnvironment(ConfigUtils.getProperty(CseConstants.KEY_DUBBO_ENVIRONMENT,
            CseConstants.DEFAULT_DUBBO_ENVIRONMENT));
        FlowControlServiceMeta.getInstance().setApp(ConfigUtils.getProperty(CseConstants.KEY_DUBBO_APP_NAME,
            CseConstants.DEFAULT_DUBBO_APP_NAME));
        FlowControlServiceMeta.getInstance().setCustomLabel(ConfigUtils.getProperty(CseConstants.KEY_DUBBO_CUSTOM_LABEL,
            CseConstants.DEFAULT_CUSTOM_LABEL));
        FlowControlServiceMeta.getInstance().setCustomLabelValue(ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_CUSTOM_LABEL_VALUE, CseConstants.DEFAULT_CUSTOM_LABEL_VALUE));
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }
}
