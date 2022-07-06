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

import com.huawei.flowcontrol.common.config.ConfigConst;
import com.huawei.flowcontrol.common.config.FlowControlConfig;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.utils.ClassUtils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 重试注入配置类
 *
 * @author zhouss
 * @since 2022-07-23
 */
@Configuration
@ConditionalOnProperty(value = "sermant.flowcontrol.retry.enabled",
        havingValue = "true", matchIfMissing = true)
public class SpringRetryConfiguration {
    /**
     * 流控重试注入
     *
     * @return resttemplate
     */
    @Bean
    @LoadBalanced
    @Primary
    @ConditionalOnClass(name = "org.springframework.web.client.RestTemplate")
    public RestTemplate restTemplate() {
        final FlowControlConfig config = PluginConfigManager.getPluginConfig(FlowControlConfig.class);
        final RetryableRestTemplate retryableRestTemplate = new RetryableRestTemplate();
        if (ConfigConst.REST_TEMPLATE_REQUEST_FACTORY_HTTP.equals(config.getRestTemplateRequestFactory())) {
            final HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();
            requestFactory.setConnectTimeout((int) config.getRestTemplateConnectTimeoutMs());
            requestFactory.setReadTimeout((int) config.getRestTemplateReadTimeoutMs());
            retryableRestTemplate.setRequestFactory(requestFactory);
        } else if (ConfigConst.REST_TEMPLATE_REQUEST_FACTORY_OK_HTTP.equals(config.getRestTemplateRequestFactory())) {
            if (ClassUtils.loadClass("okhttp3.OkHttpClient", Thread.currentThread().getContextClassLoader())
                    .isPresent()) {
                retryableRestTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory());
            }
        }
        return retryableRestTemplate;
    }
}
