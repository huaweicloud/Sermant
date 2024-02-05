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

    public static final String SIMPLE_HTTP_SERVER_TYPE = "simple";

    public static final String DEFAULT_ENCODE = "UTF-8";

    public static final int SUCCESS_STATUS = 200;

    public static final int METHOD_NOT_ALLOWED_STATUS = 405;

    public static final int SERVER_ERROR_STATUS = 500;

    public static final int NOT_FOUND_STATUS = 405;

    public static final int BAD_REQUEST_STATUS = 400;

    public static final String HTTP_PATH_DIVIDER = "/";

    private Constants() {
    }

}
