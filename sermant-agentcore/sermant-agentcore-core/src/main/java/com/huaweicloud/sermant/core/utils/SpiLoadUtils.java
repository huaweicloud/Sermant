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

package com.huaweicloud.sermant.core.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 插件spi加载工具
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021-11-16
 */
public class SpiLoadUtils {
    private SpiLoadUtils() {
    }

    /**
     * 从指定类加载起载入所有的服务
     *
     * @param serviceClass SPI接口类
     * @param classLoader  类加载器，从指定的类加载加载服务
     * @param <T>          服务的具体类型
     * @return 服务列表
     */
    public static <T> List<T> loadAll(Class<T> serviceClass, ClassLoader classLoader) {
        ServiceLoader<T> services = ServiceLoader.load(serviceClass, classLoader);
        List<T> list = new ArrayList<>();
        for (T service : services) {
            list.add(service);
        }
        list.sort(Comparator.comparingInt(o -> {
            SpiWeight weight = o.getClass().getAnnotation(SpiWeight.class);
            return weight.value();
        }));
        return list;
    }

    /**
     * 依权重获取最优实现
     *
     * @param clazz spi目标类
     * @param <T>   目标类型
     * @return 最优实现
     */
    public static <T> T getBestImpl(Class<T> clazz) {
        return getBestImpl(clazz, ClassLoader.getSystemClassLoader());
    }

    /**
     * 依权重获取最优实现 、
     *
     * @param clazz       spi目标类
     * @param classLoader 查找的ClassLoader
     * @param <T>         目标类型
     * @return 最优实现
     */
    public static <T> T getBestImpl(Class<T> clazz, ClassLoader classLoader) {
        T impl = null;
        for (T newImpl : ServiceLoader.load(clazz, classLoader)) {
            impl = getBetter(impl, newImpl);
        }
        return impl;
    }

    /**
     * 比较权重，返回真时取后者，否则取前者
     *
     * @param src 比较源
     * @param dst 比较目标
     * @param <T> 类型
     * @return 返回真时取后者，否则取前者
     */
    public static <T> T getBetter(T src, T dst) {
        return getBetter(src, dst, new WeightEqualHandler<T>() {
            @Override
            public T handle(T source, T target) {
                return source;
            }
        });
    }

    /**
     * 比较权重，返回真时取后者，否则取前者
     *
     * @param src     比较源
     * @param dst     比较目标
     * @param handler 相同权重的处理器
     * @param <T>     类型
     * @return 返回真时取后者，否则取前者
     */
    public static <T> T getBetter(T src, T dst, WeightEqualHandler<T> handler) {
        if (dst == null) {
            return src;
        } else if (src == null) {
            return dst;
        } else {
            final SpiWeight srcWeight = src.getClass().getAnnotation(SpiWeight.class);
            final int srcWeightVal = srcWeight == null ? Integer.MIN_VALUE : srcWeight.value();
            final SpiWeight dstWeight = dst.getClass().getAnnotation(SpiWeight.class);
            final int dstWeightVal = dstWeight == null ? Integer.MIN_VALUE : dstWeight.value();
            if (srcWeightVal > dstWeightVal) {
                return src;
            } else if (srcWeightVal < dstWeightVal) {
                return dst;
            } else {
                return handler.handle(src, dst);
            }
        }
    }

    /**
     * spi权重
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface SpiWeight {
        int value() default 0;
    }

    /**
     * 权重相同时的处理器
     *
     * @param <T> 类型
     */
    public interface WeightEqualHandler<T> {
        /**
         * 处理相同的方法，选择其中之一
         *
         * @param source 源
         * @param target 目标
         * @return 其中之一
         */
        T handle(T source, T target);
    }
}
