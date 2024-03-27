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
 * 常量
 *
 * @author zwmagic
 * @since 2024-02-02
 */
public class Constants {
    /**
     * 默认编码
     */
    public static final String DEFAULT_ENCODE = "UTF-8";

    /**
     * 指示HTTP消息体的MIME类型和编码格式的头部字段名
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     *  指示HTTP消息体的大小，以字节为单位的头部字段名
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * HTTP路径分隔符
     */
    public static final String HTTP_PATH_DIVIDER = "/";

    /**
     * 私有构造方法，防止外部实例化
     */
    private Constants() {
    }
}
