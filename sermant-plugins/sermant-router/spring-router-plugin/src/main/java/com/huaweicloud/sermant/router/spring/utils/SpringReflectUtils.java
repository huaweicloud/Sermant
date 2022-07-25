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

package com.huaweicloud.sermant.router.spring.utils;

import com.huaweicloud.sermant.router.common.utils.ReflectUtils;

import java.util.Map;

/**
 * 反射工具类
 *
 * @author provenceee
 * @since 2022-07-19
 */
public class SpringReflectUtils {
    private SpringReflectUtils() {
    }

    /**
     * 获取元数据
     *
     * @param obj 对象
     * @return 元数据
     */
    public static Map<String, String> getMetadata(Object obj) {
        return (Map<String, String>) ReflectUtils.invokeWithNoneParameter(obj, "getMetadata");
    }
}
