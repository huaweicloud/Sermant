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

package com.huawei.flowcontrol.common.adapte.cse.match;

import com.huawei.flowcontrol.common.adapte.cse.ResolverManager;
import com.huawei.flowcontrol.common.adapte.cse.resolver.AbstractResolver;
import com.huawei.flowcontrol.common.adapte.cse.resolver.listener.ConfigUpdateListener;
import com.huawei.flowcontrol.common.cache.Cache;
import com.huawei.flowcontrol.common.cache.TimedConcurrentMapCache;
import com.huawei.flowcontrol.common.config.CommonConst;
import com.huawei.flowcontrol.common.config.FlowControlConfig;
import com.huawei.flowcontrol.common.entity.RequestEntity;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 匹配缓存, 针对已经匹配的请求体进行缓存
 *
 * @author zhouss
 * @since 2022-07-21
 */
public class MatchedCache {
    private Cache<RequestEntity, Set<String>> cache;

    /**
     * 构造器
     */
    public MatchedCache() {
        final CacheListener cacheListener = new CacheListener();
        final Collection<AbstractResolver<?>> resolvers = ResolverManager.INSTANCE.getResolversMap().values();
        for (AbstractResolver<?> resolver : resolvers) {
            resolver.registerListener(cacheListener);
        }
    }

    /**
     * 获取缓存代理类
     *
     * @return Cache
     */
    public Cache<RequestEntity, Set<String>> getDelegate() {
        if (cache == null) {
            synchronized (MatchManager.INSTANCE) {
                final FlowControlConfig config = PluginConfigManager.getPluginConfig(FlowControlConfig.class);
                cache = new TimedConcurrentMapCache<>(config.getMaxCacheSize(),
                        config.getTimedCheckInterval() * CommonConst.S_MS_UNIT);
            }
        }
        return cache;
    }

    /**
     * 释放资源
     */
    public void release() {
        if (cache != null) {
            cache.release();
        }
    }

    /**
     * 缓存监听器, 监听所有策略变更
     *
     * @since 2022-07-21
     */
    class CacheListener implements ConfigUpdateListener {
        @Override
        public void notify(String updateKey, Map rules) {
            if (cache == null) {
                return;
            }
            final Object cacheTarget = cache.getCacheTarget();
            if (!(cacheTarget instanceof Map)) {
                return;
            }
            Map<RequestEntity, Set<String>> curCache = (Map<RequestEntity, Set<String>>) cacheTarget;
            updateAllBusinessCache(curCache);
        }

        private void updateAllBusinessCache(Map<RequestEntity, Set<String>> curCache) {
            for (Entry<RequestEntity, Set<String>> entry : curCache.entrySet()) {
                final RequestEntity requestEntity = entry.getKey();
                cache.put(requestEntity, MatchManager.INSTANCE.match(requestEntity, null));
            }
        }
    }
}
