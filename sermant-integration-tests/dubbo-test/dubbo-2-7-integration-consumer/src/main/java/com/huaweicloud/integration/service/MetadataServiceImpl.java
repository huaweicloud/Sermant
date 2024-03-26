/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.integration.service;

import com.huaweicloud.integration.client.ProviderClient;
import com.huaweicloud.integration.constants.Constant;
import com.huaweicloud.integration.utils.RpcContextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 测试接口
 *
 * @author provenceee
 * @since 2022-11-04
 */
public class MetadataServiceImpl implements MetadataService {
    private static final String PROVIDER_URL = "http://dubbo-integration-provider/hello";

    @Resource(name = "fooService")
    private FooService fooService;

    @Autowired
    @Lazy
    private ProviderClient client;

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @Override
    public String getMetadataByDubbo() {
        RpcContextUtils.setContextTagToAttachment(Constant.TAG_KEY, Constant.TAG);
        return fooService.getMetadata(false);
    }

    @Override
    public String getMetadataByFeign() {
        return client.hello();
    }

    @Override
    public String getMetadataByRest() {
        return restTemplate.getForObject(PROVIDER_URL, String.class);
    }
}