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

package com.huawei.spring.common.controller;

import com.huawei.spring.common.entity.ConfigProperty;
import com.huawei.spring.common.entity.ConfigValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置Controller
 *
 * @author zhouss
 * @since 2022-07-14
 */
@RestController
@RequestMapping("/dynamic/config")
public class ConfigController {
    @Autowired
    private ConfigValue configValue;

    @Autowired
    private ConfigProperty configProperty;

    @Autowired
    private Environment environment;

    @RequestMapping("value")
    public String getValue() {
        return configValue.getTest();
    }

    @RequestMapping("property")
    public String getProperty() {
        return configProperty.getParam1() + "," + configProperty.getParam2();
    }

    @RequestMapping("check")
    public boolean isEnableOriginConfigCenter() {
        return Boolean.parseBoolean(environment.getProperty("dynamic.config.plugin.enableOriginConfigCenter"));
    }
}
