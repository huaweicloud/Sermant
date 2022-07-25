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

package com.huaweicloud.sermant.router.common.constants;

/**
 * 常量
 *
 * @author provenceee
 * @since 2021-10-13
 */
public class RouterConstant {
    /**
     * dubbo参数索引的前缀
     */
    public static final String DUBBO_SOURCE_TYPE_PREFIX = "args";

    /**
     * 灰度发布默认ldc
     */
    public static final String ROUTER_DEFAULT_LDC = "DEFAULT_LDC";

    /**
     * 灰度发布默认版本
     */
    public static final String ROUTER_DEFAULT_VERSION = "0.0.0";

    /**
     * isEnabled匹配的方法名
     */
    public static final String ENABLED_METHOD_NAME = ".isEnabled()";

    /**
     * 注册时灰度版本的key
     */
    public static final String TAG_VERSION_KEY = "tag.version";

    /**
     * 注册时ldc的key
     */
    public static final String ROUTER_LDC_KEY = "ldc";

    /**
     * 灰度配置servicecomb的key
     */
    public static final String ROUTER_CONFIG_SERVICECOMB_KEY = "servicecomb";

    /**
     * 灰度配置routeRule的key
     */
    public static final String ROUTER_CONFIG_ROUTE_RULE_KEY = "routeRule";

    /**
     * dubbo应用灰度标签缓存名
     */
    public static final String DUBBO_CACHE_NAME = "DUBBO_ROUTE";

    /**
     * spring应用灰度标签缓存名
     */
    public static final String SPRING_CACHE_NAME = "SPRING_ROUTE";

    private RouterConstant() {
    }
}