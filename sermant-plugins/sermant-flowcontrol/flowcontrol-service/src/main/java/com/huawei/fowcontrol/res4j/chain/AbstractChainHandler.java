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

package com.huawei.fowcontrol.res4j.chain;

import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;
import com.huawei.fowcontrol.res4j.chain.context.RequestContext;

import java.util.Locale;
import java.util.Set;

/**
 * 抽象处理链, 执行顺序如下:
 * <p>onBefore -> onThrow(存在异常时执行) -> onResult</p>
 *
 * @author zhouss
 * @since 2022-07-11
 */
public abstract class AbstractChainHandler implements RequestHandler, Comparable<AbstractChainHandler> {
    private AbstractChainHandler next;

    @Override
    public void onBefore(RequestContext context, Set<String> businessNames) {
        AbstractChainHandler cur = getNextHandler(context, businessNames);
        if (cur != null) {
            cur.onBefore(context, businessNames);
        }
    }

    @Override
    public void onThrow(RequestContext context, Set<String> businessNames, Throwable throwable) {
        AbstractChainHandler cur = getNextHandler(context, businessNames);
        if (cur != null) {
            cur.onThrow(context, businessNames, throwable);
        }
    }

    @Override
    public void onResult(RequestContext context, Set<String> businessNames, Object result) {
        AbstractChainHandler cur = getNextHandler(context, businessNames);
        if (cur != null) {
            cur.onResult(context, businessNames, result);
        }
    }

    private AbstractChainHandler getNextHandler(RequestContext context, Set<String> businessNames) {
        AbstractChainHandler tmp = next;
        while (tmp != null) {
            if (!isNeedSkip(tmp, context, businessNames)) {
                break;
            }
            tmp = tmp.getNext();
        }
        return tmp;
    }

    private boolean isNeedSkip(AbstractChainHandler tmp, RequestContext context, Set<String> businessNames) {
        final RequestType direct = tmp.direct();
        if (direct != RequestType.BOTH && context.getRequestEntity().getRequestType() != direct) {
            return true;
        }
        final String skipKey = skipCacheKey(tmp);
        Boolean isSkip = context.get(skipKey, Boolean.class);
        if (isSkip == null) {
            isSkip = tmp.isSkip(context, businessNames);
            context.save(skipKey, isSkip);
        }
        return isSkip;
    }

    private String skipCacheKey(AbstractChainHandler tmp) {
        return String.format(Locale.ENGLISH, "%s_%s_skip_flag", tmp.direct(), tmp.getClass().getName());
    }

    /**
     * 请求方向, 默认均可处理, 在实际处理中将根据请求方向判断是否需要由当前的处理器处理
     *
     * @return RequestType
     */
    protected RequestType direct() {
        return RequestType.BOTH;
    }

    /**
     * 是否跳过当前handler
     *
     * @param context 请求上下文
     * @param businessNames 匹配的场景名
     * @return 是否跳过
     */
    protected boolean isSkip(RequestContext context, Set<String> businessNames) {
        return false;
    }

    @Override
    public int compareTo(AbstractChainHandler handler) {
        return getOrder() - handler.getOrder();
    }

    public void setNext(AbstractChainHandler processor) {
        this.next = processor;
    }

    public AbstractChainHandler getNext() {
        return next;
    }
}
