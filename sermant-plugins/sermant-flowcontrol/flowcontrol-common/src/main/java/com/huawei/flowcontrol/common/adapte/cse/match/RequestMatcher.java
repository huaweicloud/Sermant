/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Based on org/apache/servicecomb/governance/marker/Matcher.java from the Apache ServiceComb Java Chassis project.
 */

package com.huawei.flowcontrol.common.adapte.cse.match;

import com.huawei.flowcontrol.common.adapte.cse.match.operator.Operator;
import com.huawei.flowcontrol.common.adapte.cse.match.operator.OperatorManager;
import com.huawei.flowcontrol.common.entity.RequestEntity;

import java.util.List;
import java.util.Map;

/**
 * 单个请求匹配
 *
 * @author zhouss
 * @since 2021-11-16
 */
public class RequestMatcher implements Matcher {
    /**
     * 场景名
     */
    private String name;

    /**
     * 请求头匹配
     */
    private Map<String, RawOperator> headers;

    /**
     * 请求路径
     */
    private RawOperator apiPath;

    /**
     * 方法类型 GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS;
     */
    private List<String> method;

    /**
     * 匹配的目标服务名
     */
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, RawOperator> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, RawOperator> headers) {
        this.headers = headers;
    }

    public RawOperator getApiPath() {
        return apiPath;
    }

    public void setApiPath(RawOperator apiPath) {
        this.apiPath = apiPath;
    }

    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    /**
     * 是否匹配
     *
     * 匹配规则如下: 1.请求的方法未被包含在内，则不通过 2.请求的路劲必须匹配 3.请求头完全匹配
     *
     * @param requestEntity 请求体
     * @return 是否匹配
     */
    @Override
    public boolean match(RequestEntity requestEntity) {
        if (!isMethodMatch(requestEntity.getMethod())) {
            return false;
        }
        if (!isServiceNameMatch(requestEntity.getServiceName())) {
            return false;
        }
        if (!isHeadersMatch(requestEntity.getHeaders())) {
            return false;
        }
        return isApiPathMatch(requestEntity.getApiPath());
    }

    private boolean isServiceNameMatch(String targetServiceName) {
        if (this.serviceName == null) {
            return true;
        }
        return this.serviceName.equals(targetServiceName);
    }

    private boolean isHeadersMatch(Map<String, String> requestHeaders) {
        if (this.headers == null) {
            return true;
        }

        // 匹配请求头
        for (Map.Entry<String, RawOperator> entry : this.headers.entrySet()) {
            final String headerValue = requestHeaders.get(entry.getKey());
            if (headerValue == null) {
                return false;
            }
            if (!operatorMatch(headerValue, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean isApiPathMatch(String api) {
        if (api == null) {
            return true;
        }
        return operatorMatch(api, this.apiPath);
    }

    private boolean isMethodMatch(String requestMethod) {
        if (requestMethod == null) {
            return true;
        }
        return this.method.contains(requestMethod);
    }

    private boolean operatorMatch(String target, RawOperator operator) {
        if (operator == null || operator.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, String> entry : operator.entrySet()) {
            final Operator matchOperator = OperatorManager.INSTANCE.getOperator(entry.getKey());
            if (matchOperator == null) {
                // 无相关匹配器，数据错误！
                return false;
            }
            if (!matchOperator.match(target, entry.getValue())) {
                // 条件全部匹配
                return false;
            }
        }
        return true;
    }
}
