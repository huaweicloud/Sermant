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

package com.huawei.intergration.config.supprt;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * zk客户端，用于发布配置
 *
 * @author zhouss
 * @since 2022-07-15
 */
public class ZkClient {
    private static final String BASE_PATH = "/config/application";

    private final CuratorFramework curatorFramework;

    /**
     * 构造函数
     *
     * @param url zk地址
     */
    public ZkClient(String url) {
        curatorFramework = CuratorFrameworkFactory
                .newClient(url == null ? "127.0.0.1:2181" : url, new RetryOneTime(1000));
        curatorFramework.start();
    }

    /**
     * 发布配置
     *
     * @param path 路径
     * @param content 内容
     * @return 是否发布成功
     */
    public boolean publishConfig(String path, String content) {
        try {
            String fixPath = getFixPath(path);
            final Stat exists = curatorFramework.getZookeeperClient().getZooKeeper().exists(fixPath, false);
            if (exists != null) {
                curatorFramework.delete().forPath(fixPath);
            }
            curatorFramework.create().creatingParentsIfNeeded().forPath(fixPath, content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception exception) {
            // ignored
        }
        return false;
    }

    /**
     * 获取配置内容
     *
     * @param path 路径
     * @return 配置内容
     */
    public Optional<String> getConfig(String path) {
        try {
            return Optional.of(new String(curatorFramework.getData().forPath(getFixPath(path)), StandardCharsets.UTF_8));
        } catch (Exception exception) {
            // ignored
        }
        return Optional.empty();
    }

    private String getFixPath(String path) {
        return BASE_PATH + path;
    }

    /**
     * 关闭客户端
     */
    public void close() {
        curatorFramework.close();
    }
}
