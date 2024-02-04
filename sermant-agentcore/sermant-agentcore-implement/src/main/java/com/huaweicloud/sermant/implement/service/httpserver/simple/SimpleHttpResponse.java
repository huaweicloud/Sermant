/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR C¬ONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huaweicloud.sermant.implement.service.httpserver.simple;

import com.alibaba.fastjson.JSON;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpResponse;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zwmagic
 * @since 2024-02-02
 */
public class SimpleHttpResponse implements HttpResponse {

    private final HttpExchange exchange;
    private int status = 200;

    public SimpleHttpResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public HttpResponse status(int status) {
        this.status = status;
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
    public HttpResponse contentType(String contentType) {
        if (!contentType.contains(";")) {
            setHeader("Content-Type", contentType + ";charset=" + StandardCharsets.UTF_8);
            return this;
        }
        setHeader("Content-Type", contentType);
        return this;
    }

    @Override
    public HttpResponse contentLength(long size) {
        setHeader("Content-Length", String.valueOf(size));
        return this;
    }

    @Override
    public void writeBody(String str) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes();
        contentLength(bytes.length);
        writeBody(bytes);
    }

    @Override
    public void writeBody(Throwable ex) {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer, true));
        this.writeBody(writer.getBuffer().toString());
    }

    @Override
    public void writeBodyAsJson(Object obj) {
        writeBodyAsJson(JSON.toJSONString(obj));
    }

    @Override
    public void writeBodyAsJson(String json) {
        contentType("application/json;charset=utf-8");
        writeBody(json);
    }

    @Override
    public void writeBody(byte[] bytes) {
        OutputStream out = null;
        try {
            exchange.sendResponseHeaders(status, bytes.length);
            out = exchange.getResponseBody();
            out.write(bytes);
            exchange.close();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
