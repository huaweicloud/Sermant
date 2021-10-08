/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.lubanops.apm.plugin.flowreplay.mockclient.define.mysql.common;

import org.apache.skywalking.apm.plugin.jdbc.trace.ConnectionInfo;
import org.apache.skywalking.apm.util.StringUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * mysql connection cache
 *
 * @author luanwenfei
 * @version 0.0.1
 * @since 2021-06-03
 */
public class ConnectionCache {
    private static final ConcurrentHashMap<String, ConnectionInfo> CONNECTIONS_MAP =
        new ConcurrentHashMap<String, ConnectionInfo>();

    private static final String CONNECTION_SPLIT_STR = ",";

    public static ConnectionInfo get(String host, String port) {
        final String connStr = String.format("%s:%s", host, port);
        return CONNECTIONS_MAP.get(connStr);
    }

    public static void save(ConnectionInfo connectionInfo) {
        for (String conn : connectionInfo.getDatabasePeer().split(CONNECTION_SPLIT_STR)) {
            if (!StringUtil.isEmpty(conn)) {
                CONNECTIONS_MAP.putIfAbsent(conn, connectionInfo);
            }
        }
    }
}
