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

package com.huawei.flowcontrol.common.entity;

import java.util.Objects;

/**
 * 用于统一重写equals与hashCode方法
 *
 * @author zhouss
 * @since 2022-02-28
 */
public abstract class AbstractRequestEntity implements RequestEntity {
    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 请求类型
     */
    private RequestType requestType;

    private long timestamp;

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractRequestEntity that = (AbstractRequestEntity) obj;
        return Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getApiPath(), that.getApiPath())
            && Objects.equals(getMethod(), that.getMethod()) && Objects.equals(getServiceName(), that.getServiceName())
                && getRequestType() == that.getRequestType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHeaders(), getApiPath(), getMethod());
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
