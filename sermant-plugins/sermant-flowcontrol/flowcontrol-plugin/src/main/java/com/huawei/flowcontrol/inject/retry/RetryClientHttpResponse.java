/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.inject.retry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 可重试异常
 *
 * @author zhouss
 * @since 2022-07-23
 */
public class RetryClientHttpResponse extends AbstractClientHttpResponse {
    private final String msg;

    private final int code;

    /**
     * 构造器
     *
     * @param msg 错误信息
     * @param code 错误码
     */
    public RetryClientHttpResponse(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    @Override
    public int getRawStatusCode() {
        return code;
    }

    @Override
    public String getStatusText() {
        return msg;
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/text");
        return httpHeaders;
    }
}
