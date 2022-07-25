/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.dubbo.service;

import com.huaweicloud.sermant.router.common.addr.AddrCache;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.config.label.LabelCache;
import com.huaweicloud.sermant.router.config.label.entity.CurrentTag;
import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;

/**
 * RegistrationInterceptor的service
 *
 * @author provenceee
 * @since 2021-11-24
 */
public class RegistrationServiceImpl implements RegistrationService {
    /**
     * 设置地址-版本缓存
     *
     * @param addr 地址
     * @param version 版本
     */
    @Override
    public void setRegisterVersionCache(String addr, String version) {
        AddrCache.setRegisterVersionCache(addr, version);
    }

    /**
     * 设置自身版本缓存
     *
     * @param version 版本
     */
    @Override
    public void setRegisterVersion(String version) {
        RouterConfiguration configuration = LabelCache.getLabel(RouterConstant.DUBBO_CACHE_NAME);
        CurrentTag currentTag = configuration.getCurrentTag();
        currentTag.setRegisterVersion(version);
    }
}