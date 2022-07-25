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

package com.huaweicloud.sermant.router.config.label.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * 匹配规则
 *
 * @author provenceee
 * @since 2021-10-27
 */
public class Match {
    /**
     * 来源，即目标应用的上游应用
     */
    private String source;

    /**
     * dubbo为接口名，spring为url路径
     */
    private String path;

    /**
     * 是否全匹配
     */
    private boolean fullMatch = true;

    /**
     * dubbo参数规则
     */
    @JSONField(deserializeUsing = ValueMatchDeserializer.class)
    private Map<String, List<MatchRule>> args;

    /**
     * spring http header规则
     */
    @JSONField(deserializeUsing = ValueMatchDeserializer.class)
    private Map<String, List<MatchRule>> headers;

    /**
     * spring http parameter规则
     */
    @JSONField(deserializeUsing = ValueMatchDeserializer.class)
    private Map<String, List<MatchRule>> parameters;

    /**
     * spring http cookie规则
     */
    @JSONField(deserializeUsing = ValueMatchDeserializer.class)
    private Map<String, List<MatchRule>> cookie;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFullMatch() {
        return fullMatch;
    }

    public void setFullMatch(boolean fullMatch) {
        this.fullMatch = fullMatch;
    }

    public Map<String, List<MatchRule>> getArgs() {
        return args;
    }

    public void setArgs(Map<String, List<MatchRule>> args) {
        this.args = args;
    }

    public Map<String, List<MatchRule>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<MatchRule>> headers) {
        this.headers = headers;
    }

    public Map<String, List<MatchRule>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<MatchRule>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, List<MatchRule>> getCookie() {
        return cookie;
    }

    public void setCookie(Map<String, List<MatchRule>> cookie) {
        this.cookie = cookie;
    }
}