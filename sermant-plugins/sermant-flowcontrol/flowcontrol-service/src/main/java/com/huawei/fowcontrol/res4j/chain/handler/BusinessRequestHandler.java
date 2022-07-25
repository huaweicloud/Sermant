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

package com.huawei.fowcontrol.res4j.chain.handler;

import com.huawei.flowcontrol.common.adapte.cse.match.MatchManager;
import com.huawei.fowcontrol.res4j.chain.AbstractChainHandler;
import com.huawei.fowcontrol.res4j.chain.context.ChainContext;
import com.huawei.fowcontrol.res4j.chain.context.RequestContext;

import java.util.Set;

/**
 * 业务匹配处理器,优先级最高
 *
 * @author zhouss
 * @since 2022-07-05
 */
public class BusinessRequestHandler extends AbstractChainHandler {
    private static final String MATCHED_BUSINESS_NAMES = "__MATCHED_BUSINESS_NAMES__";

    @Override
    public void onBefore(RequestContext context, Set<String> businessNames) {
        final Set<String> matchBusinessNames = MatchManager.INSTANCE.matchWithCache(context.getRequestEntity());
        if (matchBusinessNames == null || matchBusinessNames.isEmpty()) {
            return;
        }
        context.save(MATCHED_BUSINESS_NAMES, matchBusinessNames);
        super.onBefore(context, matchBusinessNames);
    }

    @Override
    public void onThrow(RequestContext context, Set<String> businessNames, Throwable throwable) {
        final Set<String> matchBusinessNames = getMatchedBusinessNames(context);
        if (matchBusinessNames == null || matchBusinessNames.isEmpty()) {
            return;
        }
        super.onThrow(context, matchBusinessNames, throwable);
    }

    @Override
    public void onResult(RequestContext context, Set<String> businessNames, Object result) {
        final Set<String> matchBusinessNames = getMatchedBusinessNames(context);
        if (matchBusinessNames == null || matchBusinessNames.isEmpty()) {
            return;
        }
        try {
            super.onResult(context, matchBusinessNames, result);
        } finally {
            ChainContext.getThreadLocalContext(context.getSourceName()).remove(MATCHED_BUSINESS_NAMES);
        }
    }

    private Set<String> getMatchedBusinessNames(RequestContext context) {
        return context.get(MATCHED_BUSINESS_NAMES, Set.class);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
