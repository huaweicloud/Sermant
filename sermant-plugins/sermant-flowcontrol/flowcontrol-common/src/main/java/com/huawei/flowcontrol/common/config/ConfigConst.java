/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.common.config;

/**
 * 配置常量类
 *
 * @author zhouss
 * @since 2022-01-28
 */
public class ConfigConst {
    /**
     * sentinel版本
     */
    public static final String SENTINEL_VERSION = "sentinel.version";

    /**
     * sentinel配置参数 流控规则zookeeper地址
     */
    public static final String ZOOKEEPER_ADDRESS = "flowcontrol.zookeeper.address";

    /**
     * sentinel配置参数 zookeeper流控规则配置路径
     */
    public static final String ZOOKEEPER_PATH = "flowcontrol.zookeeper.path";

    /**
     * sentinel配置参数 心跳发送默认间隔时间，单位毫秒
     */
    public static final String DEFAULT_HEARTBEAT_INTERVAL = "default.heartbeat.interval";

    /**
     * sentinel配置参数 流控信息数据发送默认间隔时间，单位毫秒
     */
    public static final String DEFAULT_METRIC_INTERVAL = "default.metric.interval";

    /**
     * sentinel配置参数 启动后初始加载流控信息数据的时间段时长
     */
    public static final String METRIC_INITIAL_DURATION = "metric.initial.duration";

    /**
     * sentinel配置参数 未提供查询流控信息数据结束时间的默认加载数据条数
     */
    public static final String METRIC_MAX_LINE = "metric.maxLine";

    /**
     * sentinel配置参数 查询流控数据时,睡眠一段时间，等待限流数据写入文件再查询
     */
    public static final String METRIC_SLEEP_TIME = "metric.sleep.time";

    /**
     * kafka配置参数 连接服务端地址
     */
    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";

    /**
     * kafka配置参数 key序列化
     */
    public static final String KAFKA_KEY_SERIALIZER = "kafka.key.serializer";

    /**
     * kafka配置参数 value序列化
     */
    public static final String KAFKA_VALUE_SERIALIZER = "kafka.value.serializer";

    /**
     * kafka配置参数 流控信息数据发送topic名称
     */
    public static final String KAFKA_METRIC_TOPIC = "kafka.metric.topic";

    /**
     * kafka配置参数 心跳数据发送topic名称
     */
    public static final String KAFKA_HEARTBEAT_TOPIC = "kafka.heartbeat.topic";

    /**
     * kafka配置参数 producer需要server接收到数据之后发出的确认接收的信号 ack 0,1,all
     */
    public static final String KAFKA_ACKS = "kafka.acks";

    /**
     * kafka配置参数 控制生产者发送请求最大大小,默认1M （这个参数和Kafka主机的message.max.bytes 参数有关系）
     */
    public static final String KAFKA_MAX_REQUEST_SIZE = "kafka.max.request.size";

    /**
     * kafka配置参数 生产者内存缓冲区大小
     */
    public static final String KAFKA_BUFFER_MEMORY = "kafka.buffer.memory";

    /**
     * kafka配置参数 重发消息次数
     */
    public static final String KAFKA_RETRIES = "kafka.retries";

    /**
     * kafka配置参数 客户端将等待请求的响应的最大时间
     */
    public static final String KAFKA_REQUEST_TIMEOUT_MS = "kafka.request.timeout.ms";

    /**
     * kafka配置参数 最大阻塞时间，超过则抛出异常
     */
    public static final String KAFKA_MAX_BLOCK_MS = "kafka.max.block.ms";

    /**
     * jaas配置常量
     */
    public static final String KAFKA_JAAS_CONFIG_CONST = "sasl.jaas.config";

    /**
     * kafka认证配置
     */
    public static final String KAFKA_JAAS_CONFIG = "";

    /**
     * SASL鉴权方式常量
     */
    public static final String KAFKA_SASL_MECHANISM_CONST = "sasl.mechanism";

    /**
     * SASL鉴权机制
     */
    public static final String KAFKA_SASL_MECHANISM = "";

    /**
     * 加密协议常量
     */
    public static final String KAFKA_SECURITY_PROTOCOL_CONST = "security.protocol";

    /**
     * 加密协议，目前支持SASL_SSL协议
     */
    public static final String KAFKA_SECURITY_PROTOCOL = "";

    /**
     * ssl truststore文件存放位置常量
     */
    public static final String KAFKA_SSL_TRUSTSTORE_LOCATION_CONST = "ssl.truststore.location";

    /**
     * ssl truststore文件存放位置
     */
    public static final String KAFKA_SSL_TRUSTSTORE_LOCATION = "";

    /**
     * ssl truststore密码常量
     */
    public static final String KAFKA_SSL_TRUSTSTORE_PASSWORD_CONST = "ssl.truststore.password";

    /**
     * ssl truststore密码配置
     */
    public static final String KAFKA_SSL_TRUSTSTORE_PASSWORD = "";

    /**
     * 域名常量
     */
    public static final String KAFKA_IDENTIFICATION_ALGORITHM_CONST = "ssl.endpoint.identification.algorithm";

    /**
     * 域名不校验
     */
    public static final String KAFKA_IDENTIFICATION_ALGORITHM = "";

    /**
     * 是否通过ssl认证
     */
    public static final boolean IS_KAFKA_SSL = false;

    /**
     * 配置中心 zookeeper路径
     */
    public static final String CONFIG_ZOOKEEPER_PATH = "config.zookeeper.path";

    /**
     * 默认环境
     */
    public static final String CONFIG_PROFILE_ACTIVE_DEFAULT = "config.profile.active.default";

    /**
     * 对接的配置中心类型
     */
    public static final String CONFIG_CENTER_TYPE = "flowcontrol.configCenter.type";

    /**
     * 配置中心servicecomb-kie地址
     */
    public static final String CONFIG_KIE_ADDRESS = "config.kie.address";

    /**
     * servicecomb-kie获取配置的URL
     */
    public static final String KIE_RULES_URI = "/v1/default/kie/kv";

    /**
     * spring应用名称
     */
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";

    /**
     * 项目名称
     */
    public static final String PROJECT_NAME = "project.name";

    /**
     * 默认最大的缓存数
     */
    public static final int DEFAULT_MAX_CACHE_SIZE = 1000;

    /**
     * 默认的缓存过期检查时间, 单位秒, 默认一个小时
     */
    public static final long DEFAULT_TIME_CACHE_CHECK_INTERVAL = 3600L;

    /**
     * 连接超时时间
     */
    public static final long CONNECT_TIMEOUT_MS = 1000L;

    /**
     * 响应超时时间
     */
    public static final long SOCKET_READ_TIMEOUT_MS = 1000L;

    /**
     * rest template OKHTTP 协议
     */
    public static final String REST_TEMPLATE_REQUEST_FACTORY_OK_HTTP = "okHttp";

    /**
     * 请求httpclient协议
     */
    public static final String REST_TEMPLATE_REQUEST_FACTORY_HTTP = "http";

    private ConfigConst() {
    }
}

