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

package com.huaweicloud.sermant.core.service.httpserver.api;

import java.util.Map;

/**
 * Http response
 *
 * @author zwmagic
 * @since 2024-02-02
 */
public interface HttpResponse {
    /**
     * 获取响应状态码
     *
     * @return 响应状态码
     */
    int status();

    /**
     * 设置响应状态码
     *
     * @param status 响应状态码
     * @return 响应对象
     */
    HttpResponse status(int status);

    /**
     * 添加响应头
     *
     * @param name 头部名称
     * @param value 头部值
     * @return 响应对象
     */
    HttpResponse addHeader(String name, String value);

    /**
     * 设置响应头
     *
     * @param name 头部名称
     * @param value 头部值
     * @return 响应对象
     */
    HttpResponse setHeader(String name, String value);

    /**
     * 设置响应头集合
     *
     * @param headers 响应头集合
     * @return 响应对象
     */
    HttpResponse setHeaders(Map<String, String> headers);

    /**
     * 设置响应内容类型
     *
     * @param contentType 内容类型
     * @return 响应对象
     */
    HttpResponse contentType(String contentType);

    /**
     * 设置响应内容长度
     *
     * @param size 内容长度
     * @return 响应对象
     */
    HttpResponse contentLength(long size);

    /**
     * 写入响应体（字节数组）
     *
     * @param bytes 字节数组
     */
    void writeBody(byte[] bytes);

    /**
     * 写入响应体（字符串）
     *
     * @param str 字符串
     */
    void writeBody(String str);

    /**
     * 写入响应体（异常）
     *
     * @param ex 异常对象
     */
    void writeBody(Throwable ex);

    /**
     * 写入响应体（JSON字符串）
     *
     * @param json JSON字符串
     */
    void writeBodyAsJson(String json);

    /**
     * 写入响应体（JSON对象）
     *
     * @param obj JSON对象
     */
    void writeBodyAsJson(Object obj);
}
