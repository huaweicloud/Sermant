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

package com.huawei.intergration.config.supprt;

import com.alibaba.fastjson.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Kie客户端
 *
 * @author zhouss
 * @since 2022-07-14
 */
public class KieClient {
    private final RestTemplate restTemplate;

    private final String url;

    /**
     * 构造函数
     *
     * @param restTemplate 请求器
     */
    public KieClient(RestTemplate restTemplate) {
        this(restTemplate, null);
    }

    /**
     * 构造函数
     *
     * @param restTemplate 请求器
     * @param url 地址
     */
    public KieClient(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.url = url == null ? "http://127.0.0.1:30110/v1/default/kie/kv" : url;
    }

    /**
     * 发布配置
     *
     * @param key 键
     * @param value 值
     * @return 是否发布成功
     */
    public boolean publishConfig(String key, String value) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(new KieRequest(key, value)), headers);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url, entity, JSONObject.class);
            return responseEntity.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException ex) {
            // ignored
        }
        return false;
    }

    /**
     * kie请求
     *
     * @since 2022-07-12
     */
    static class KieRequest implements Serializable {
        private String key;
        private String value;
        private Map<String, String> labels = new HashMap<String, String>();
        private String status = "enabled";

        {
            labels.put("app", "default");
            labels.put("environment", "development");
        }

        public KieRequest(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Map<String, String> getLabels() {
            return labels;
        }

        public void setLabels(Map<String, String> labels) {
            this.labels = labels;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
