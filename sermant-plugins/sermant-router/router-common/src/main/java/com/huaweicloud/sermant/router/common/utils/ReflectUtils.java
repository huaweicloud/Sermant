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

package com.huaweicloud.sermant.router.common.utils;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 反射工具类
 *
 * @author provenceee
 * @since 2022-02-07
 */
public class ReflectUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private ReflectUtils() {
    }

    /**
     * 获取私有字段值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @return 私有字段值
     */
    public static Optional<Object> getFieldValue(Object obj, String fieldName) {
        Class<?> currClass = obj.getClass();
        while (currClass != Object.class) {
            try {
                return Optional.ofNullable(getAccessibleObject(currClass.getDeclaredField(fieldName)).get(obj));
            } catch (NoSuchFieldException e) {
                currClass = currClass.getSuperclass();
            } catch (IllegalAccessException e) {
                LOGGER.warning("Cannot get the field, fieldName is " + fieldName);
                return Optional.empty();
            }
        }
        LOGGER.warning("Cannot get the field, fieldName is " + fieldName);
        return Optional.empty();
    }

    /**
     * 获取权限检查类
     *
     * @param object 权限检查对象
     * @param <T> 权限检查对象
     * @return 返回setAccessible(true)之后的对象
     */
    public static <T extends AccessibleObject> T getAccessibleObject(T object) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> {
            object.setAccessible(true);
            return object;
        });
    }

    /**
     * 反射调用无参方法并且返回字符串
     *
     * @param obj 对象
     * @param name 方法名
     * @return 值
     */
    public static String invokeWithNoneParameterAndReturnString(Object obj, String name) {
        return (String) invokeWithNoneParameter(obj, name);
    }

    /**
     * 反射调用无参方法
     *
     * @param obj 对象
     * @param name 方法名
     * @return 值
     */
    public static Object invokeWithNoneParameter(Object obj, String name) {
        return invoke(obj.getClass(), obj, name, null, null).orElse(null);
    }

    /**
     * 反射调用有一个参数的方法
     *
     * @param obj 对象
     * @param name 方法名
     * @param parameter 参数
     * @param parameterClass 参数类型
     * @return 值
     */
    public static Object invokeWithParameter(Object obj, String name, Object parameter, Class<?> parameterClass) {
        return invoke(obj.getClass(), obj, name, parameter, parameterClass).orElse(null);
    }

    private static Optional<Object> invoke(Class<?> invokeClass, Object obj, String name, Object parameter,
        Class<?> parameterClass) {
        try {
            if (parameterClass == null) {
                return Optional.ofNullable(invokeClass.getMethod(name).invoke(obj));
            }
            return Optional.ofNullable(invokeClass.getMethod(name, parameterClass).invoke(obj, parameter));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // 因版本的原因，有可能会找不到方法，所以可以忽略这些错误
        }
        return Optional.empty();
    }
}