/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.dubbotest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = {"com.huawei.dubbotest.service","com.huawei.dubbotest.controller"})
@ImportResource({"classpath:dubbo/consumer.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class DubboAApplication {
    public static void main(String[] args) {
        System.out.println("====================start=======================");
        SpringApplication.run(DubboAApplication.class);
        System.out.println("=====================end========================");
    }
}
