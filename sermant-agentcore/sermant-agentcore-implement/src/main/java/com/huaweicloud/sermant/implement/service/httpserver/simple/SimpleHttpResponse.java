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

package com.huaweicloud.sermant.implement.service.httpserver.simple;

import com.huaweicloud.sermant.core.service.httpserver.api.HttpResponse;
import com.huaweicloud.sermant.core.service.httpserver.exception.HttpServerException;
import com.huaweicloud.sermant.implement.service.httpserver.common.Constants;
import com.huaweicloud.sermant.implement.service.httpserver.common.HttpCodeEnum;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 简单http响应
 *
 * @author zwmagic
 * @since 2024-02-02
 */
public class SimpleHttpResponse implements HttpResponse {
    private final HttpExchange exchange;

    private int status = HttpCodeEnum.SUCCESS.getCode();

    /**
     * 构造函数，用于创建一个SimpleHttpResponse对象。
     *
     * @param exchange HttpExchange对象，用于与服务器进行通信
     */
    public SimpleHttpResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public HttpResponse setStatus(int code) {
        this.status = code;
        return this;
    }

    @Override
    public HttpResponse addHeader(String name, String value) {
        exchange.getResponseHeaders().add(name, value);
        return this;
    }

    @Override
    public HttpResponse setHeader(String name, String value) {
        exchange.getResponseHeaders().set(name, value);
        return this;
    }

    @Override
    public HttpResponse setHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            setHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public HttpResponse setContentType(String contentType) {
        if (!contentType.contains(";")) {
            setHeader(Constants.CONTENT_TYPE, contentType + ";charset=" + StandardCharsets.UTF_8);
            return this;
        }
        setHeader(Constants.CONTENT_TYPE, contentType);
        return this;
    }

    @Override
    public HttpResponse setContentLength(long size) {
        setHeader(Constants.CONTENT_LENGTH, String.valueOf(size));
        return this;
    }

    @Override
    public void writeBody(Throwable ex) {
        this.writeBody(ex.getMessage());
    }

    @Override
    public void writeBody(byte[] bytes) {
        try (OutputStream out = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(status, bytes.length);
            out.write(bytes);
        } catch (Exception ex) {
            throw new HttpServerException(HttpCodeEnum.SERVER_ERROR.getCode(), ex);
        }
    }

    @Override
    public void writeBody(String str) {
        byte[] bytes = str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
        setContentLength(bytes.length);
        writeBody(bytes);
    }

    @Override
    public void writeBodyAsJson(String json) {
        setContentType("application/json;charset=utf-8");
        writeBody(json);
    }

    @Override
    public void writeBodyAsJson(Object obj) {
        writeBodyAsJson(JSON.toJSONString(obj));
    }
}