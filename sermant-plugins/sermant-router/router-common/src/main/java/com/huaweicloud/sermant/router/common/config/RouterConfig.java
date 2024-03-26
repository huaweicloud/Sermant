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

package com.huaweicloud.sermant.router.common.config;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.config.common.ConfigFieldKey;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 路由配置
 *
 * @author provenceee
 * @since 2021-11-18
 */
@ConfigTypeKey("router.plugin")
public class RouterConfig implements PluginConfig {
    /**
     * 路由版本
     */
    private String routerVersion = RouterConstant.ROUTER_DEFAULT_VERSION;

    /**
     * 区域
     */
    private String zone;

    /**
     * 是否初始化dubbo区域路由，兼容性开关，开启后，会在dubbo的配置缓存中初始化一条以zone匹配的同标签路由
     */
    @ConfigFieldKey("enabled-dubbo-zone-router")
    private boolean enabledDubboZoneRouter;

    /**
     * 是否初始化spring cloud区域路由，兼容性开关，开启后，会在spring cloud的配置缓存中初始化一条以zone匹配的同标签路由
     */
    @ConfigFieldKey("enabled-spring-zone-router")
    private boolean enabledSpringZoneRouter;

    /**
     * 是否适配注册插件
     */
    @ConfigFieldKey("enabled-registry-plugin-adaptation")
    private boolean enabledRegistryPluginAdaptation;

    /**
     * 是否使用请求信息做路由
     */
    @ConfigFieldKey("use-request-router")
    private boolean useRequestRouter;

    /**
     * 使用请求信息做路由时的tags
     */
    @ConfigFieldKey("request-tags")
    private List<String> requestTags;

    /**
     * 其它配置
     */
    private Map<String, String> parameters;

    /**
     * 需要解析的请求头的tag
     */
    @ConfigFieldKey("parse-header-tag")
    private String parseHeaderTag;

    /**
     * compatibility router config 1.0, default is false not support
     */
    @ConfigFieldKey("enabled-previous-rule")
    private boolean enabledPreviousRule = false;

    /**
     * 构造方法
     */
    public RouterConfig() {
        ServiceMeta serviceMeta = ConfigManager.getConfig(ServiceMeta.class);
        if (serviceMeta == null) {
            return;
        }
        this.routerVersion = serviceMeta.getVersion();
        this.zone = serviceMeta.getZone();
        this.parameters = serviceMeta.getParameters();
    }

    public String getRouterVersion() {
        return routerVersion;
    }

    public void setRouterVersion(String routerVersion) {
        this.routerVersion = routerVersion;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isEnabledDubboZoneRouter() {
        return enabledDubboZoneRouter;
    }

    public void setEnabledDubboZoneRouter(boolean enabledDubboZoneRouter) {
        this.enabledDubboZoneRouter = enabledDubboZoneRouter;
    }

    public boolean isEnabledSpringZoneRouter() {
        return enabledSpringZoneRouter;
    }

    public void setEnabledSpringZoneRouter(boolean enabledSpringZoneRouter) {
        this.enabledSpringZoneRouter = enabledSpringZoneRouter;
    }

    public boolean isEnabledRegistryPluginAdaptation() {
        return enabledRegistryPluginAdaptation;
    }

    public void setEnabledRegistryPluginAdaptation(boolean enabledRegistryPluginAdaptation) {
        this.enabledRegistryPluginAdaptation = enabledRegistryPluginAdaptation;
    }

    public boolean isUseRequestRouter() {
        return useRequestRouter;
    }

    public void setUseRequestRouter(boolean useRequestRouter) {
        this.useRequestRouter = useRequestRouter;
    }

    public List<String> getRequestTags() {
        return requestTags == null ? Collections.emptyList() : requestTags;
    }

    /**
     * 请求头在http请求中，会统一转成小写
     *
     * @param requestTags 使用请求信息做路由时的tags
     */
    public void setRequestTags(List<String> requestTags) {
        if (requestTags != null) {
            requestTags.replaceAll(tag -> tag.toLowerCase(Locale.ROOT));
            this.requestTags = requestTags;
        } else {
            this.requestTags = null;
        }
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParseHeaderTag() {
        return parseHeaderTag;
    }

    public void setParseHeaderTag(String parseHeaderTag) {
        this.parseHeaderTag = parseHeaderTag;
    }

    public boolean isEnabledPreviousRule() {
        return enabledPreviousRule;
    }

    public void setEnabledPreviousRule(boolean enabledPreviousRule) {
        this.enabledPreviousRule = enabledPreviousRule;
    }
}