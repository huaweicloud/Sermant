/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.integration.service;

import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 测试接口
 *
 * @author provenceee
 * @since 2022-04-28
 */
public class BarServiceImpl implements BarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarServiceImpl.class);

    @Value("${service_meta_zone:${SERVICE_META_ZONE:${service.meta.zone:bar}}}")
    private String zone;

    @Value("${service_meta_version:${SERVICE_META_VERSION:${service.meta.version:1.0.0}}}")
    private String version;

    @Value("${dubbo.application.name}")
    private String name;

    @Value("${service_meta_parameters:${SERVICE_META_PARAMETERS:${service.meta.parameters:}}}")
    private String parameters;

    @Override
    public String bar(String str) {
        return "bar1:" + str;
    }

    @Override
    public Map<String, Object> getAttachments() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>(getAttachmentsByReflect());
        map.put("version", version);
        result.put(name, map);
        return result;
    }

    /**
     * 版本兼容问题，只能用反射获取
     *
     * @return attachments
     */
    private Map<String, Object> getAttachmentsByReflect() {
        RpcContext context = RpcContext.getContext();
        try {
            Field field = context.getClass().getDeclaredField("attachments");
            Object obj = AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
                field.setAccessible(true);
                return field;
            }).get(context);
            return (Map<String, Object>) obj;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.warn(String.format(Locale.ENGLISH, "Cannot get attachments field: %s", e.getMessage()));
            try {
                Method method = context.getClass().getDeclaredMethod("getAttachments");
                method.setAccessible(true);
                return (Map<String, Object>) method.invoke(context);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                LOGGER.error("Cannot get attachments.", e);
            }
        }
        return Collections.emptyMap();
    }
}