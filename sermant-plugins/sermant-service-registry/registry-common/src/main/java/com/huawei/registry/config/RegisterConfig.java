/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.registry.config;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spring注册插件配置
 *
 * @author zhouss
 * @since 2021-12-16
 */
@ConfigTypeKey(value = "servicecomb.service")
public class RegisterConfig implements PluginConfig {
    /**
     * sc注册中心地址 多个地址使用逗号隔开
     */
    private String address = "http://127.0.0.1:30100";

    /**
     * kie命名空间
     */
    private String project = ConfigConstants.COMMON_DEFAULT_VALUE;

    /**
     * 服务实例心跳发送间隔
     */
    private int heartbeatInterval = ConfigConstants.DEFAULT_HEARTBEAT_INTERVAL;

    /**
     * 心跳重试次数
     */
    private int heartbeatRetryTimes = ConfigConstants.DEFAULT_HEARTBEAT_RETRY_TIMES;

    /**
     * 拉取实例时间间隔
     */
    private int pullInterval = ConfigConstants.DEFAULT_PULL_INTERVAL;

    /**
     * sc app配置
     */
    private String application = "sermant";

    /**
     * sc 环境配置
     */
    private String environment = "production";

    /**
     * 默认sc版本
     */
    private String version = "1.0.0";

    /**
     * 注册中心类型
     */
    private RegisterType registerType = RegisterType.SERVICE_COMB;

    /**
     * 是否开启sc的加密 作为配置类，使用布尔类型不可使用is开头，否则存在配置无法读取的问题
     */
    private boolean sslEnabled = false;

    /**
     * 是否开启迁移模式
     */
    private boolean openMigration = false;

    /**
     * spring注册开关
     */
    private boolean enableSpringRegister = false;

    /**
     * dubbo注册开关
     */
    private boolean enableDubboRegister = false;

    /**
     * 是否启用区域发现
     */
    private boolean enableZoneAware = false;

    /**
     * 数据中心名称
     */
    private String dataCenterName = ConfigConstants.COMMON_DEFAULT_VALUE;

    /**
     * 数据中心 区域
     */
    private String dataCenterRegion = ConfigConstants.COMMON_DEFAULT_VALUE;

    /**
     * 数据中心 去
     */
    private String dataCenterAvailableZone = ConfigConstants.COMMON_DEFAULT_VALUE;

    /**
     * 是否支持跨app访问实例
     */
    private boolean allowCrossApp = false;

    /**
     * spring cloud zone
     * 若未配置默认使用系统环境变量的zone, 即spring.cloud.loadbalancer.zone
     */
    private String zone;

    /**
     * 是否忽略契约差异
     */
    private boolean ignoreSwaggerDifferent = false;

    /**
     * dubbo参数白名单
     */
    private List<String> governanceParametersWhiteList = Collections.singletonList("timeout");

    /**
     * dubbo注册时的接口级参数key
     */
    private List<String> interfaceKeys;

    /**
     * 服务级别参数，形如k1,v1;k2,v2
     */
    private String parameter;

    /**
     * 构造方法
     */
    public RegisterConfig() {
        final ServiceMeta serviceMeta = ConfigManager.getConfig(ServiceMeta.class);
        if (serviceMeta == null) {
            return;
        }
        this.environment = serviceMeta.getEnvironment();
        this.application = serviceMeta.getApplication();
        this.project = serviceMeta.getProject();
        this.version = serviceMeta.getVersion();
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isAllowCrossApp() {
        return allowCrossApp;
    }

    public void setAllowCrossApp(boolean allowCrossApp) {
        this.allowCrossApp = allowCrossApp;
    }

    public String getDataCenterName() {
        return dataCenterName;
    }

    public void setDataCenterName(String dataCenterName) {
        this.dataCenterName = dataCenterName;
    }

    public String getDataCenterRegion() {
        return dataCenterRegion;
    }

    public void setDataCenterRegion(String dataCenterRegion) {
        this.dataCenterRegion = dataCenterRegion;
    }

    public String getDataCenterAvailableZone() {
        return dataCenterAvailableZone;
    }

    public void setDataCenterAvailableZone(String dataCenterAvailableZone) {
        this.dataCenterAvailableZone = dataCenterAvailableZone;
    }

    public boolean isEnableZoneAware() {
        return enableZoneAware;
    }

    public void setEnableZoneAware(boolean enableZoneAware) {
        this.enableZoneAware = enableZoneAware;
    }

    public boolean isEnableSpringRegister() {
        return enableSpringRegister;
    }

    public void setEnableSpringRegister(boolean enableSpringRegister) {
        this.enableSpringRegister = enableSpringRegister;
    }

    public boolean isEnableDubboRegister() {
        return enableDubboRegister;
    }

    public void setEnableDubboRegister(boolean enableDubboRegister) {
        this.enableDubboRegister = enableDubboRegister;
    }

    public boolean isOpenMigration() {
        return openMigration;
    }

    public void setOpenMigration(boolean openMigration) {
        this.openMigration = openMigration;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public RegisterType getRegisterType() {
        return registerType;
    }

    public void setRegisterType(RegisterType registerType) {
        this.registerType = registerType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getAddress() {
        return address;
    }

    /**
     * 获取逗号分隔后的地址列表
     *
     * @return 地址列表
     */
    public List<String> getAddressList() {
        if (StringUtils.isBlank(address)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(address.split(",")));
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getHeartbeatRetryTimes() {
        return heartbeatRetryTimes;
    }

    public void setHeartbeatRetryTimes(int heartbeatRetryTimes) {
        this.heartbeatRetryTimes = heartbeatRetryTimes;
    }

    public int getPullInterval() {
        return pullInterval;
    }

    public void setPullInterval(int pullInterval) {
        this.pullInterval = pullInterval;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isIgnoreSwaggerDifferent() {
        return ignoreSwaggerDifferent;
    }

    public void setIgnoreSwaggerDifferent(boolean ignoreSwaggerDifferent) {
        this.ignoreSwaggerDifferent = ignoreSwaggerDifferent;
    }

    public List<String> getGovernanceParametersWhiteList() {
        return governanceParametersWhiteList;
    }

    public void setGovernanceParametersWhiteList(List<String> governanceParametersWhiteList) {
        this.governanceParametersWhiteList = governanceParametersWhiteList;
    }

    public List<String> getInterfaceKeys() {
        return interfaceKeys;
    }

    public void setInterfaceKeys(List<String> interfaceKeys) {
        this.interfaceKeys = interfaceKeys;
    }

    /**
     * 获取注册参数
     *
     * @return 注册参数
     */
    public Map<String, String> getParameterMap() {
        if (StringUtils.isBlank(parameter)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        String[] kvs = parameter.trim().split(",");
        for (String kv : kvs) {
            String[] arr = kv.trim().split(":");
            if (arr.length > 0) {
                map.put(arr[0].trim(), arr.length > 1 ? arr[1].trim() : "");
            }
        }
        return map;
    }

    public String getParameter() {
        return this.parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}