/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.hercules.service.influxdb.measurement.servermonitor;

import com.huawei.hercules.service.influxdb.measurement.CommonMetricInfluxEntity;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

/**
 * server monitor network Influxdb持久化实体
 */
@Measurement(name = "server_monitor_network")
public class NetworkInfluxEntity extends CommonMetricInfluxEntity {
    /**
     * 采集周期内的每秒读字节数
     */
    @Column(name = "read_bytes_per_second")
    private Long readBytesPerSec;

    /**
     * 采集周期内的每秒写字节数
     */
    @Column(name = "write_bytes_per_second")
    private Long writeBytesPerSec;

    /**
     * 采集周期内的每秒读包数
     */
    @Column(name = "read_packages_per_second")
    private Long readPackagesPerSec;

    /**
     * 采集周期内的每秒写包数
     */
    @Column(name = "write_packages_per_second")
    private Long writePackagesPerSec;
}
