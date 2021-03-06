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

package com.huawei.dubbotest.controller;

import com.huawei.dubbotest.service.TestInterface;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

/**
 * 测试接口
 *
 * @since 2022-03-16
 */
@RestController
public class DubboController {
    @Resource(name = "testInterface")
    private TestInterface testInterface;

    /**
     * 测试接口
     *
     * @return 测试信息
     */
    @GetMapping("/test")
    public String test27() {
        Map<String, String> resultMap = new TreeMap<>(testInterface.test());
        return resultMap.toString();
    }
}