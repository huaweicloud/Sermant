/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol.common.adapte.cse.entity;

/**
 * 服务原信息类 - 单例 如果依赖sdk的话，可考虑直接替换为MicroserviceMeta {@see org.apache.servicecomb.governance.MicroserviceMeta}
 *
 * @author zhouss
 * @since 2021-11-16
 */
public class FlowControlServiceMeta {
    private static final FlowControlServiceMeta INSTANCE = new FlowControlServiceMeta();

    /**
     * sc app名
     */
    private String app;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 版本
     */
    private String version;

    /**
     * 环境
     */
    private String environment;

    /**
     * sc自定义值
     */
    private String customLabelValue;

    /**
     * sc自定义标签
     */
    private String customLabel;

    /**
     * kie-project
     */
    private String project;

    /**
     * 是否为dubbo应用
     */
    private boolean isDubboService;

    private FlowControlServiceMeta() {
    }

    public boolean isDubboService() {
        return isDubboService;
    }

    public void setDubboService(boolean dubboService) {
        isDubboService = dubboService;
    }

    public static FlowControlServiceMeta getInstance() {
        return INSTANCE;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCustomLabelValue() {
        return customLabelValue;
    }

    public void setCustomLabelValue(String customLabelValue) {
        this.customLabelValue = customLabelValue;
    }

    public String getCustomLabel() {
        return customLabel;
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    /**
     * 当前数据是否都已经获取
     *
     * @return boolean
     */
    public boolean isReady() {
        return isCustomInfoReady() && isServiceInfoReady();
    }

    private boolean isServiceInfoReady() {
        return serviceName != null && environment != null && app != null;
    }

    private boolean isCustomInfoReady() {
        return project != null && customLabel != null && customLabelValue != null;
    }
}
