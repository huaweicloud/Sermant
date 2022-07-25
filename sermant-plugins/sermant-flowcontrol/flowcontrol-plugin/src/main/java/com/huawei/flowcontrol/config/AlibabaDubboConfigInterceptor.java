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
 */

package com.huawei.flowcontrol.config;

import com.huawei.flowcontrol.common.adapte.cse.constants.CseConstants;
import com.huawei.flowcontrol.common.adapte.cse.entity.FlowControlServiceMeta;
import com.huawei.flowcontrol.common.config.FlowControlConfig;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

/**
 * apache dubbo配置拦截
 *
 * @author zhouss
 * @since 2022-01-28
 */
public class AlibabaDubboConfigInterceptor extends AbstractInterceptor {
    /**
     * 此处代码与{@link ApacheDubboConfigInterceptor}相同
     * <p>由于使用的是不同的权限定名框架，因此<h3>不可抽出，且不可放在除拦截器之外的类执行该段逻辑（类加载器问题）</h3></p>
     */
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        final FlowControlConfig pluginConfig = PluginConfigManager.getPluginConfig(FlowControlConfig.class);
        FlowControlServiceMeta.getInstance().setDubboService(true);
        if (!pluginConfig.isUseCseRule() || !pluginConfig.isBaseSdk()) {
            return context;
        }
        FlowControlServiceMeta.getInstance().setVersion(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_VERSION,
            CseConstants.DEFAULT_DUBBO_VERSION));
        FlowControlServiceMeta.getInstance().setProject(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_KIE_PROJECT,
            CseConstants.DEFAULT_PROJECT));
        FlowControlServiceMeta.getInstance().setServiceName(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_SERVICE_NAME,
            CseConstants.DEFAULT_DUBBO_SERVICE_NAME));
        FlowControlServiceMeta.getInstance().setEnvironment(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_ENVIRONMENT,
            CseConstants.DEFAULT_DUBBO_ENVIRONMENT));
        FlowControlServiceMeta.getInstance().setApp(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_APP_NAME,
            CseConstants.DEFAULT_DUBBO_APP_NAME));
        FlowControlServiceMeta.getInstance().setCustomLabel(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_CUSTOM_LABEL,
            CseConstants.DEFAULT_CUSTOM_LABEL));
        FlowControlServiceMeta.getInstance().setCustomLabelValue(com.alibaba.dubbo.common.utils.ConfigUtils.getProperty(
            CseConstants.KEY_DUBBO_CUSTOM_LABEL_VALUE,
            CseConstants.DEFAULT_CUSTOM_LABEL_VALUE));
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return context;
    }
}
