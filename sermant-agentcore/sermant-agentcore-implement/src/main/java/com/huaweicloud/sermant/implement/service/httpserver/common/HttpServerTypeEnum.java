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

package com.huaweicloud.sermant.implement.service.httpserver.common;

/**
 * HTTP服务器类型枚举
 * @author zwmagic
 */
public enum HttpServerTypeEnum {
    /**
     * 简单服务器类型, 使用JDK自带的Http Server
     */
    SIMPLE("simple");

    /**
     * 类型标识
     */
    private final String type;

    /**
     *  枚举构造函数
     * @param type 枚举类型的字符串标识
     */
    HttpServerTypeEnum(String type) {
        this.type = type;
    }

    /**
     * 获取枚举类型的字符串标识
     * @return 类型标识字符串
     */
    public String getType() {
        return type;
    }
}
