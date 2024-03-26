/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.integration.utils;

import org.apache.dubbo.rpc.RpcContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 请求上下文设置标签路由
 *
 * @author chengyouling
 * @since 2024-03-20
 */
public class RpcContextUtils {
    private RpcContextUtils() {

    }

    /**
     * 设置请求上下文标签
     *
     * @param key 标签键
     * @param value 标签值
     */
    public static void setContextTagToAttachment(String key, String value) {
        RpcContext context = RpcContext.getContext();
        try {
            Method clientMethod = context.getClass().getDeclaredMethod("getClientAttachment");
            clientMethod.setAccessible(true);
            ((RpcContext) clientMethod.invoke(context)).setAttachment(key, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            context.setAttachment(key, value);
        }
    }
}
