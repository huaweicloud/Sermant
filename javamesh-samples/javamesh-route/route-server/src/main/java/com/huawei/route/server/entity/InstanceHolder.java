/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.route.server.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Instance列表包装类
 *
 * @author zhouss
 * @since 2021-10-27
 */
@Data
public class InstanceHolder<T extends AbstractInstance> {
    /**
     * 唯一标志
     */
    private String key;

    /**
     * 实例列表
     * key : ip@port
     * value : instance
     */
    private Map<String, T> instances;

    /**
     * 针对属于不同服务数据分组
     * key ： 原生服务名
     * value : 实例ip端口集合
     */
    private Map<String, Set<String>> serviceGroup;

    public T getInstance(String instanceKey) {
        if (instances == null || StringUtils.isEmpty(instanceKey)) {
            return null;
        }
        return instances.get(instanceKey);
    }

    public void remove(String instanceKey) {
        if (instances == null || StringUtils.isEmpty(instanceKey)) {
            return;
        }
        instances.remove(instanceKey);
    }

    public void update(String instanceKey, T instance) {
        if (instance == null || StringUtils.isEmpty(instanceKey)) {
            return;
        }
        if (instances == null) {
            instances = new ConcurrentHashMap<>();
        }
        instances.put(instanceKey, instance);
    }

    public void updateServiceGroup(String key, Set<String> set) {
        if (StringUtils.isEmpty(key) || CollectionUtils.isEmpty(set)) {
            return;
        }
        if (serviceGroup == null) {
            serviceGroup = new ConcurrentHashMap<>();
        }
        serviceGroup.put(key, set);
    }
}
