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

package com.huaweicloud.sermant.router.dubbo.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.service.ServiceManager;
import com.huaweicloud.sermant.router.common.utils.ReflectUtils;
import com.huaweicloud.sermant.router.dubbo.service.RegistrationService;

import org.apache.servicecomb.service.center.client.model.Microservice;
import org.apache.servicecomb.service.center.client.model.MicroserviceInstance;

import java.util.List;
import java.util.Optional;

/**
 * 增强RegistrationListener类的notify方法
 *
 * @author provenceee
 * @since 2021-11-08
 */
public class RegistrationInterceptor extends AbstractInterceptor {
    private static final String MICROSERVICE_FIELD_NAME = "microservice";

    private static final String DUBBO_PREFIX = "dubbo://";

    private static final int LIST_INDEX = 2;

    private final RegistrationService registrationService;

    /**
     * 构造方法
     */
    public RegistrationInterceptor() {
        registrationService = ServiceManager.getService(RegistrationService.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object[] arguments = context.getArguments();
        if (arguments.length <= LIST_INDEX || !(arguments[LIST_INDEX] instanceof List<?>)) {
            return context;
        }
        Object obj = context.getObject();
        Optional<Object> microservice = ReflectUtils.getFieldValue(obj, MICROSERVICE_FIELD_NAME);
        if (!microservice.isPresent()) {
            return context;
        }
        registrationService.setRegisterVersion(((Microservice) microservice.get()).getVersion());
        List<MicroserviceInstance> instances = (List<MicroserviceInstance>) arguments[LIST_INDEX];
        for (MicroserviceInstance instance : instances) {
            for (String endpoint : instance.getEndpoints()) {
                registrationService.setRegisterVersionCache(endpoint.substring(DUBBO_PREFIX.length()),
                    instance.getVersion());
            }
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }
}