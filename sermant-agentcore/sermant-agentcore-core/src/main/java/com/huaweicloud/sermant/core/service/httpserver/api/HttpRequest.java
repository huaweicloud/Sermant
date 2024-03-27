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

import com.huaweicloud.sermant.core.service.httpserver.exception.HttpServerException;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Http Request
 *
 * @author zwmagic
 * @since 2024-02-02
 */
public interface HttpRequest {
    /**
     * 获取URI。
     *
     * @return URI对象
     */
    URI getUri();

    /**
     * 经过解析后的请求path
     *
     * @return 请求的path
     */
    String getPath();

    /**
     * 原始的请求path
     *
     * @return 请求的path
     */
    String getOriginalPath();

    /**
     * HTTP 方法
     *
     * @return HttpMethod，如 GET POST等
     */
    String getMethod();

    /**
     * 获取内容类型。
     *
     * @return 内容类型
     */
    String getContentType();

    /**
     * 获取IP地址。
     *
     * @return IP地址
     */
    String getIp();

    /**
     * 获取指定名称的header的第一个值。
     *
     * @param name header的key
     * @return 返回指定name的header的第一个值
     */
    String getFirstHeader(String name);

    /**
     * 获取指定名称的header的第一个值。
     *
     * @param name header的key
     * @param defaultValue 当值是null时，返回默认值
     * @return 返回指定name的header的第一个值
     */
    String getFirstHeader(String name, String defaultValue);

    /**
     * 获取所有的header。
     *
     * @return 返回一个包含所有header的Map，其中key为header的名称，value为该header的值列表
     */
    Map<String, List<String>> getHeaders();

    /**
     * 指定name的参数
     * <pre>
     *     eg: http://127.0.0.1:8080/api/v1/test?k1=v1&k2=v2， params为k1=v1&k2=v2
     * </pre>
     *
     * @param name 参数名
     * @return 返回指定name的参数
     */
    String getParam(String name);

    /**
     * 指定name的参数
     * <pre>
     *     eg: http://127.0.0.1:8080/api/v1/test?k1=v1&k2=v2， params为k1=v1&k2=v2
     * </pre>
     *
     * @param name 参数名
     * @param defaultValue 当值是null时，返回默认值
     * @return 返回指定name的参数
     */
    String getParam(String name, String defaultValue);

    /**
     * 指定name的参数
     * <pre>
     *     eg: http://127.0.0.1:8080/api/v1/test?k1=v1&k2=v2， params为k1=v1&k2=v2
     * </pre>
     *
     * @return 返回指定name的参数
     */
    Map<String, String> getParams();

    /**
     * 获取请求的主体内容。
     *
     * @return 请求的主体内容
     * @throws HttpServerException 如果发生HTTP服务异常
     */
    String getBody() throws HttpServerException;

    /**
     * 从请求体中解析出指定类型的对象。
     *
     * @param <T> 泛型类型
     * @param clazz 要解析的类
     * @return 解析出的对象
     * @throws HttpServerException 如果发生HTTP服务异常
     */
    <T> T getBody(Class<T> clazz) throws HttpServerException;

    /**
     * 获取请求的主体内容。
     *
     * @param charset 字符集
     * @return 请求的主体内容
     * @throws HttpServerException 如果发生HTTP服务异常
     */
    String getBody(Charset charset) throws HttpServerException;

    /**
     * 将请求体转换为字节数组。
     *
     * @return 请求体的字节数组
     * @throws HttpServerException 如果发生HTTP服务异常
     */
    byte[] getBodyAsBytes() throws HttpServerException;

    /**
     * 从请求体中解析出指定类型的对象列表。
     *
     * @param <T> 泛型类型
     * @param clazz 要解析的类
     * @return 解析出的对象列表
     * @throws HttpServerException 如果发生HTTP服务异常
     */
    <T> List<T> getBodyAsList(Class<T> clazz) throws HttpServerException;

    /**
     * 获取请求体的输入流。
     *
     * @return 请求体的输入流
     */
    InputStream getBodyAsStream();
}
