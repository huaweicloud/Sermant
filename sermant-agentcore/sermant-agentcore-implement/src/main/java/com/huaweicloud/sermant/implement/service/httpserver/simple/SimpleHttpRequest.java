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

import com.huaweicloud.sermant.core.service.httpserver.api.HttpRequest;
import com.huaweicloud.sermant.core.service.httpserver.exception.HttpServerException;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.implement.service.httpserver.common.Constants;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
 * 简单http请求实现
 *
 * @author zwmagic
 * @since 2024-02-02
 */
public class SimpleHttpRequest implements HttpRequest {
    private static final int BODY_BYTE_SIZE = 512;

    private final HttpExchange exchange;

    private String originalPath;

    private String path;

    private final Map<String, String> params = new HashMap<>();

    /**
     * 构造函数，用于创建一个SimpleHttpRequest对象。
     *
     * @param exchange HttpExchange对象，用于与服务器进行通信
     */
    public SimpleHttpRequest(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public URI getUri() {
        return exchange.getRequestURI();
    }

    @Override
    public String getPath() {
        if (path != null) {
            return path;
        }
        String[] array = getOriginalPath().split(Constants.HTTP_PATH_DIVIDER);
        List<String> phases = Stream.of(array).filter(phase -> !phase.isEmpty()).collect(Collectors.toList());
        path = Constants.HTTP_PATH_DIVIDER + String.join(Constants.HTTP_PATH_DIVIDER, phases);
        return path;
    }

    @Override
    public String getOriginalPath() {
        if (originalPath != null) {
            return originalPath;
        }
        String uri = getUri().toString();
        originalPath = uri.split("\\?")[0];
        return originalPath;
    }

    @Override
    public String getMethod() {
        return exchange.getRequestMethod();
    }

    @Override
    public String getContentType() {
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
        List<String> headers = exchange.getRequestHeaders().get(name);
        return CollectionUtils.isEmpty(headers) ? null : headers.get(0);
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
    public String getParam(String name) {
        return getParams().get(name);
    }

    @Override
    public String getParam(String name, String def) {
        return getParams().getOrDefault(name, def);
    }

    @Override
    public Map<String, String> getParams() {
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
                    params.put(URLDecoder.decode(keyVal[0], Constants.DEFAULT_ENCODE),
                            URLDecoder.decode(keyVal[1], Constants.DEFAULT_ENCODE));
                } else {
                    params.put(URLDecoder.decode(keyVal[0], Constants.DEFAULT_ENCODE), "");
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return params;
    }

    @Override
    public String getBody() throws HttpServerException {
        return getBody(StandardCharsets.UTF_8);
    }

    /**
     * 获取body内容
     */
    @Override
    public String getBody(Charset charset) throws HttpServerException {
        StringBuilder body = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getBodyAsStream(), charset));
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (Exception e) {
            throw new HttpServerException(Constants.SERVER_ERROR_STATUS, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new HttpServerException(Constants.SERVER_ERROR_STATUS, e);
                }
            }
        }
        return body.toString();
    }

    @Override
    public <T> T getBody(Class<T> clazz) throws HttpServerException {
        String body = getBody();
        return JSONObject.parseObject(body, clazz);
    }

    @Override
    public <T> List<T> getBodyAsList(Class<T> clazz) throws HttpServerException {
        String body = getBody();
        return JSON.parseArray(body, clazz);
    }

    @Override
    public byte[] getBodyAsBytes() throws HttpServerException {
        try (InputStream ins = getBodyAsStream()) {
            if (ins == null) {
                return new byte[0];
            }
            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[BODY_BYTE_SIZE];
            while ((len = ins.read(buf)) != -1) {
                outs.write(buf, 0, len);
            }
            return outs.toByteArray();
        } catch (Exception e) {
            throw new HttpServerException(Constants.SERVER_ERROR_STATUS, e);
        }
    }

    @Override
    public InputStream getBodyAsStream() {
        return exchange.getRequestBody();
    }
}
