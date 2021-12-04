/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved
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

package com.huawei.flowcontrol.core.datasource.kie.rule.isolate;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.huawei.flowcontrol.adapte.cse.rule.isolate.IsolateThreadRule;
import com.huawei.flowcontrol.adapte.cse.rule.isolate.IsolateThreadRuleManager;
import com.huawei.flowcontrol.core.datasource.kie.rule.RuleWrapper;

import java.util.List;

/**
 * 隔离仓规则
 *
 * @author zhouss
 * @since 隔离仓规则
 */
public class IsolateThreadRuleWrapper extends RuleWrapper {
    /**
     * 注册流控规则到流控规则管理器
     *
     * @param dataSource 数据源
     */
    @Override
    public void registerRuleManager(AbstractDataSource<?, ?> dataSource) {
        SentinelProperty<?> property = dataSource.getProperty();
        if (property != null) {
            IsolateThreadRuleManager.register2Property((SentinelProperty<List<IsolateThreadRule>>) property);
        }
    }

    /**
     * 获取规则数据的类信息
     *
     * @return 返回class对象
     */
    @Override
    protected Class<?> getRuleClass() {
        return IsolateThreadRule.class;
    }
}