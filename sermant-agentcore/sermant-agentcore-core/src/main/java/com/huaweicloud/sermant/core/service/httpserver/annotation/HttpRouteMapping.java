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

package com.huaweicloud.sermant.core.service.httpserver.annotation;

import com.huaweicloud.sermant.core.service.httpserver.api.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * http route映射注解，类似 springmvc @RequestMapping
 * @author zwmagic
 * @since 2024-02-02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface HttpRouteMapping {

    /**
     * 请求方法
     *
     * @return 请求方法
     */
    HttpMethod method();

    /**
     * 请求路径
     *
     * @return 请求路径
     */
    String path();

    /**
     * 描述信息
     *
     * @return 描述信息
     */
    String desc() default "";

}