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

package com.huaweicloud.sermant.router.dubbo.strategy;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.utils.StringUtils;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * 规则策略
 *
 * @author provenceee
 * @since 2021-10-13
 */
public abstract class TypeStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * 获取参数值
     *
     * @param arg 参数
     * @param type 获取参数的方式
     * @return 参数值
     */
    public abstract Optional<String> getValue(Object arg, String type);

    /**
     * 是否匹配规则
     *
     * @param type 规则表达式
     * @return 是否匹配
     */
    public boolean isMatch(String type) {
        if (!checkType(type)) {
            return false;
        }
        try {
            Integer.parseInt(getKey(type));
            return true;
        } catch (NumberFormatException e) {
            LOGGER.warning("type " + type + " is not a number.");
            return false;
        }
    }

    /**
     * 开始字符串
     *
     * @return 开始字符串
     */
    public abstract String getBeginFlag();

    /**
     * 结束字符串
     *
     * @return 结束字符串
     */
    public abstract String getEndFlag();

    /**
     * 检查获取参数的类型
     *
     * @param type 类型
     * @return 是否合法
     */
    public boolean checkType(String type) {
        return StringUtils.isExist(type) && type.startsWith(getBeginFlag()) && type.endsWith(getEndFlag());
    }

    /**
     * 获取参数的key值
     *
     * @param type 获取参数类型
     * @return key值
     */
    public String getKey(String type) {
        return type.substring(getBeginFlag().length(), type.length() - getEndFlag().length());
    }
}