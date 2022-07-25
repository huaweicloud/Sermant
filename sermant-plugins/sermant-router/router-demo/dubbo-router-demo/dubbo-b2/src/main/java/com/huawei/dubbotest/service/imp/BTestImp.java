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

package com.huawei.dubbotest.service.imp;

import com.huawei.dubbotest.domain.Test;
import com.huawei.dubbotest.service.BTest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BTestImp implements BTest {
    @Value("${test.version}")
    private String version;

    @Override
    public Map<String, String> testEmpty(long id, String name) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", this.version);
        System.out.println("BTest:============" + map);
        return map;
    }

    @Override
    public Map<String, String> testObject(Test test) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", this.version);
        System.out.println("BTest:============" + map);
        return map;
    }

    @Override
    public Map<String, String> testEnabled(Test test) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", this.version);
        System.out.println("BTest:============" + map);
        return map;
    }

    @Override
    public Map<String, String> testArray(String[] array) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", this.version);
        System.out.println("BTest:============" + map);
        return map;
    }

    @Override
    public Map<String, String> testList(List<String> list) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", this.version);
        System.out.println("BTest:============" + map);
        return map;
    }

    @Override
    public Map<String, String> testMap(Map<String, String> map1) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", this.version);
        System.out.println("BTest:============" + map);
        return map;
    }
}
