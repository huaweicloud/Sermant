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

package com.huawei.flowcontrol.inject;

import com.huawei.flowcontrol.common.adapte.cse.ResolverManager;
import com.huawei.flowcontrol.common.adapte.cse.constants.CseConstants;
import com.huawei.flowcontrol.common.adapte.cse.entity.FlowControlServiceMeta;
import com.huawei.flowcontrol.common.config.FlowControlConfig;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

/**
 * 基于spring配置
 *
 * @author zhouss
 * @since 2022-06-28
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlowControlSpringConfiguration {
    private final AtomicBoolean isLoadedRule = new AtomicBoolean();

    @Value("${dubbo.application.name:${spring.application.name:application}}")
    private String serviceName;

    @Autowired
    private Environment environment;

    /**
     * 初始化流控先关配置
     */
    @PostConstruct
    public void init() {
        initConfig();
        loadRuleFromEnvironment();
    }

    private void initConfig() {
        if (FlowControlServiceMeta.getInstance().isDubboService()) {
            return;
        }
        final FlowControlConfig pluginConfig = PluginConfigManager.getPluginConfig(FlowControlConfig.class);
        if (pluginConfig.isUseCseRule() && pluginConfig.isBaseSdk()) {
            FlowControlServiceMeta.getInstance().setProject(environment.getProperty(CseConstants.KEY_SPRING_KIE_PROJECT,
                    CseConstants.DEFAULT_PROJECT));
            FlowControlServiceMeta.getInstance().setServiceName(environment.getProperty(
                    CseConstants.KEY_SPRING_SERVICE_NAME));
            FlowControlServiceMeta.getInstance().setEnvironment(environment.getProperty(
                    CseConstants.KEY_SPRING_ENVIRONMENT));
            FlowControlServiceMeta.getInstance().setApp(environment.getProperty(CseConstants.KEY_SPRING_APP_NAME));
            FlowControlServiceMeta.getInstance().setCustomLabel(environment.getProperty(
                    CseConstants.KEY_SPRING_CUSTOM_LABEL,
                    CseConstants.DEFAULT_CUSTOM_LABEL));
            FlowControlServiceMeta.getInstance().setCustomLabelValue(environment.getProperty(
                    CseConstants.KEY_SPRING_CUSTOM_LABEL_VALUE, CseConstants.DEFAULT_CUSTOM_LABEL_VALUE));
            FlowControlServiceMeta.getInstance().setVersion(environment.getProperty(CseConstants.KEY_SPRING_VERSION));
        } else {
            FlowControlServiceMeta.getInstance().setServiceName(serviceName);
        }
    }

    /**
     * 从spring环境变量读取流控策略
     */
    private void loadRuleFromEnvironment() {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return;
        }
        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        if (isLoadedRule.compareAndSet(false, true)) {
            for (PropertySource<?> next : getPropertySources(configurableEnvironment.getPropertySources())) {
                loadRuleFromSource((EnumerablePropertySource<?>) next);
            }
        }
    }

    /**
     * 这里对配置源倒序排序，保证获取的配置优先级与spring一致
     *
     * @param propertySources 配置源
     * @return 倒序后的配置源
     */
    private List<PropertySource<?>> getPropertySources(MutablePropertySources propertySources) {
        final LinkedList<PropertySource<?>> result = new LinkedList<>();
        for (PropertySource<?> next : propertySources) {
            if (next instanceof EnumerablePropertySource) {
                result.addFirst(next);
            }
        }
        return result;
    }

    private void loadRuleFromSource(EnumerablePropertySource<?> source) {
        final String[] propertyNames = source.getPropertyNames();
        for (String propertyName : propertyNames) {
            if (!ResolverManager.INSTANCE.isTarget(propertyName)) {
                continue;
            }
            ResolverManager.INSTANCE.resolve(propertyName, String.valueOf(source.getProperty(propertyName)), false);
        }
    }
}
