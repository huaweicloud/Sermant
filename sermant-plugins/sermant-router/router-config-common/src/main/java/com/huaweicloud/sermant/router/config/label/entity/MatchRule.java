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

package com.huaweicloud.sermant.router.config.label.entity;

/**
 * 参数匹配
 *
 * @author provenceee
 * @since 2021-10-27
 */
public class MatchRule {
    /**
     * 值匹配规则
     */
    private ValueMatch valueMatch;

    /**
     * 是否区分大小写
     */
    private boolean caseInsensitive;

    /**
     * dubbo获取参数的类型: [留空], [.name], [.isEnabled()], [[0]], [.get(0)], [.get("key")]
     */
    private String type;

    public ValueMatch getValueMatch() {
        return valueMatch;
    }

    public void setValueMatch(ValueMatch valueMatch) {
        this.valueMatch = valueMatch;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}