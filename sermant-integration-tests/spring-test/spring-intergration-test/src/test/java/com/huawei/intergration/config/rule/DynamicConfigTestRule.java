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

package com.huawei.intergration.config.rule;

import com.huawei.intergration.config.constants.Constants;
import com.huawei.intergration.config.enums.DynamicTestType;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Locale;

/**
 * 动态配置测试规则
 *
 * @author zhouss
 * @since 2022-07-15
 */
public abstract class DynamicConfigTestRule implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final String type = System.getProperty(Constants.DYNAMIC_CONFIG_TEST_TYPE_KEY);
                if (type == null) {
                    base.evaluate();
                    return;
                }

                final DynamicTestType dynamicTestType = DynamicTestType.valueOf(type.toUpperCase(Locale.ROOT));
                if (isSupport(dynamicTestType)) {
                    base.evaluate();
                }
            }
        };
    }

    /**
     * 是否支持
     *
     * @param dynamicTestType 测试配置中心类型
     * @return 是否支持
     */
    protected abstract boolean isSupport(DynamicTestType dynamicTestType);
}
