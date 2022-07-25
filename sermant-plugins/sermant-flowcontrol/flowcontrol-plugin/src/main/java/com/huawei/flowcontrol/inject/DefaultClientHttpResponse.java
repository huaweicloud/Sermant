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

package com.huawei.flowcontrol.inject;

import com.huawei.flowcontrol.common.entity.FlowControlResult;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 默认响应, 在接管RestTemplate请求，触发治理策略后的默认响应
 *
 * @author zhouss
 * @since 2022-07-21
 */
public class DefaultClientHttpResponse implements ClientHttpResponse {
    private final FlowControlResult flowControlResult;

    /**
     * 构造器
     *
     * @param flowControlResult 流控修过
     */
    public DefaultClientHttpResponse(FlowControlResult flowControlResult) {
        this.flowControlResult = flowControlResult;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(flowControlResult.getResult().getCode());
    }

    @Override
    public int getRawStatusCode() {
        return flowControlResult.getResult().getCode();
    }

    @Override
    public String getStatusText() {
        return flowControlResult.buildResponseMsg();
    }

    @Override
    public void close() {
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(flowControlResult.buildResponseMsg().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/text");
        return httpHeaders;
    }
}
