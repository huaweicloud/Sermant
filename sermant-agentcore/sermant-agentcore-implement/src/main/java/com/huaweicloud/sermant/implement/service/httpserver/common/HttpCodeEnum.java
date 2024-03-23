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
 * HTTP状态码枚举类，用于定义常见的HTTP响应状态码及其对应的信息。
 * @author zwmagic
 */
public enum HttpCodeEnum {
    // 请求成功
    SUCCESS(200, "SUCCESS"),
    // 客户端请求有误，服务器无法理解
    BAD_REQUEST(400, "BAD REQUEST"),
    // 请求被服务器拒绝，没有权限
    FORBIDDEN(403, "FORBIDDEN"),
    // 请求需要用户验证，即未授权
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    // 服务器找不到请求的资源
    NOT_FOUND(404, "Not Found"),
    // 请求方法不被允许
    METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),
    // 服务器内部错误，无法完成请求
    SERVER_ERROR(500, "SERVER ERROR");

    // HTTP状态码
    private final int code;

    // 状态码对应的文本信息
    private final String message;

    /**
     * 构造函数，用于初始化枚举实例。
     * @param code 状态码
     * @param message 状态码对应的文本信息
     */
    HttpCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取HTTP状态码。
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取状态码对应的文本信息。
     * @return 文本信息
     */
    public String getMessage() {
        return message;
    }
}