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
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRequest;
import com.huaweicloud.sermant.core.service.httpserver.exception.HttpServerException;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zwmagic
 * @since 2024-02-02
 */
public class SimpleHttpRequest implements HttpRequest {

    private final HttpExchange exchange;

    private String originalPath;

    private String path;

    private final Map<String, String> params = new HashMap<>();

    public SimpleHttpRequest(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public URI getUri() {
        return exchange.getRequestURI();
    }

    @Override
    public String path() {
        if (path != null) {
            return path;
        }
        String[] array = originalPath().split("/");
        List<String> phases = Stream.of(array).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        path = "/" + String.join("/", phases);
        return path;
    }

    @Override
    public String originalPath() {
        if (originalPath != null) {
            return originalPath;
        }
        String uri = getUri().toString();
        originalPath = uri.split("\\?")[0];
        return originalPath;
    }

    @Override
    public String method() {
        return exchange.getRequestMethod();
    }

    @Override
    public String contentType() {
        return getFirstHeader("Content-Type");
    }

    @Override
    public String getIp() {
        String ip = getFirstHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = getFirstHeader("X-Forwarded-For");
        }
        return ip;
    }

    @Override
    public String getFirstHeader(String name) {
        List<String> value = exchange.getRequestHeaders().get(name);
        return value == null || value.isEmpty() ? null : value.get(0);
    }

    @Override
    public String getFirstHeader(String name, String defaultValue) {
        String value = getFirstHeader(name);
        return value == null ? defaultValue : value;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return exchange.getRequestHeaders();
    }

    @Override
    public String param(String name) {
        return params().get(name);
    }

    @Override
    public String param(String name, String def) {
        return params().getOrDefault(name, def);
    }

    @Override
    public Map<String, String> params() {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        if (StringUtils.isEmpty(query)) {
            return params;
        }
        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyVal = pair.split("=");
                if (keyVal.length > 1) {
                    params.put(URLDecoder.decode(keyVal[0], "UTF-8"), URLDecoder.decode(keyVal[1], "UTF-8"));
                } else {
                    params.put(URLDecoder.decode(keyVal[0], "UTF-8"), "");
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return params;
    }

    @Override
    public String body() throws HttpServerException {
        return body(StandardCharsets.UTF_8);
    }

    /**
     * 获取body内容
     */
    @Override
    public String body(Charset charset) throws HttpServerException {
        StringBuilder body = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(bodyAsStream(), charset));
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            reader.close();
        } catch (Exception e) {
            throw new HttpServerException(500, e);
        }
        return body.toString();
    }

    @Override
    public <T> T body(Class<T> clazz) throws HttpServerException {
        String body = body();
        return JSONObject.parseObject(body, clazz);
    }

    @Override
    public <T> List<T> bodyAsList(Class<T> clazz) throws HttpServerException {
        String body = body();
        return JSON.parseArray(body, clazz);
    }

    @Override
    public byte[] bodyAsBytes() throws HttpServerException {
        try (InputStream ins = bodyAsStream()) {
            if (ins == null) {
                return null;
            }
            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[512];
            while ((len = ins.read(buf)) != -1) {
                outs.write(buf, 0, len);
            }
            return outs.toByteArray();
        } catch (Exception e) {
            throw new HttpServerException(500, e);
        }
    }

    @Override
    public InputStream bodyAsStream() {
        return exchange.getRequestBody();
    }

}
