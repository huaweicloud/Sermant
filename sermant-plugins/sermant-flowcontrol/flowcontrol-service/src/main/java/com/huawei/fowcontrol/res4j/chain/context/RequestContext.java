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

package com.huawei.fowcontrol.res4j.chain.context;

import com.huawei.flowcontrol.common.entity.RequestEntity;
import com.huawei.fowcontrol.res4j.chain.HandlerConstants;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 请求上下文
 *
 * @author zhouss
 * @since 2022-07-11
 */
public class RequestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final Map<String, Object> localMap = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<String, RequestContext>> threadLocal;

    private final String sourceName;

    private RequestEntity requestEntity;

    /**
     * 构造函数
     *
     * @param threadLocal 线程变量
     * @param sourceName 源名称
     */
    public RequestContext(ThreadLocal<Map<String, RequestContext>> threadLocal, String sourceName) {
        this.threadLocal = threadLocal;
        this.sourceName = sourceName;
    }

    /**
     * 保存线程变量
     *
     * @param name 变量名称
     * @param target 保存对象
     */
    public void save(String name, Object target) {
        if (name == null || target == null) {
            LOGGER.warning("ThreadLocal name or target can not be empty!");
            return;
        }
        localMap.put(formatKey(name), target);
    }

    /**
     * 获取线程变量
     *
     * @param name 名称
     * @param <T>  返回类型
     * @param clazz 指定类型
     * @return 结果
     */
    public <T> T get(String name, Class<T> clazz) {
        return (T) localMap.get(formatKey(name));
    }

    /**
     * 移除线程变量
     *
     * @param name 变量名称
     */
    public void remove(String name) {
        localMap.remove(formatKey(name));
        if (localMap.isEmpty()) {
            threadLocal.remove();
        }
    }

    /**
     * 清理说有数据
     */
    public void clear() {
        localMap.clear();
    }

    private boolean isNeedFormat(String name) {
        return !HandlerConstants.THREAD_LOCAL_KEY_PREFIX.equals(name);
    }

    private String formatKey(String name) {
        if (!isNeedFormat(name)) {
            return name;
        }
        final String keyPrefix = get(HandlerConstants.THREAD_LOCAL_KEY_PREFIX, String.class);
        if (keyPrefix == null) {
            return name;
        }
        return keyPrefix + name;
    }

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    public String getSourceName() {
        return sourceName;
    }
}
