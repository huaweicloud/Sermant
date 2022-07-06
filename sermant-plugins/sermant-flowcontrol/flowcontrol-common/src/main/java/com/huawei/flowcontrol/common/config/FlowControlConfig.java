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

import com.huawei.flowcontrol.common.enums.FlowFramework;
import com.huawei.flowcontrol.common.enums.MetricSendWay;

import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;

/**
 * 流控配置类
 *
 * @author zhouss
 * @since 2022-01-28
 */
@ConfigTypeKey("flow.control.plugin")
public class FlowControlConfig implements PluginConfig {
    /**
     * 流控插件kafka地址
     */
    private String kafkaBootstrapServers = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094";

    /**
     * 心跳发送默认间隔时间，单位毫秒
     */
    private long heartbeatInterval = CommonConst.FLOW_CONTROL_HEARTBEAT_INTERVAL;

    /**
     * 流控信息数据发送默认间隔时间，单位毫秒
     */
    private long metricInterval = CommonConst.FLOW_CONTROL_METRIC_INTERVAL;

    /**
     * 启动后初始加载流控信息数据的时间段时长
     */
    private long metricInitialDuration = CommonConst.METRIC_INITIAL_DURATION;

    /**
     * 未提供查询流控信息数据结束时间的默认加载数据条数
     */
    private long metricMaxLine = CommonConst.METRIC_MAX_LINE;

    /**
     * 查询流控数据时,睡眠一段时间，等待限流数据写入文件再查询
     */
    private long metricSleepTime = CommonConst.METRIC_SLEEP_TIME;

    /**
     * kafka配置参数 key序列化
     */
    private String kafkaKeySerializer = "org.apache.kafka.common.serialization.StringSerializer";

    /**
     * kafka配置参数 value序列化
     */
    private String kafkaValueSerializer = "org.apache.kafka.common.serialization.StringSerializer";

    /**
     * kafka配置参数 流控信息数据发送topic名称
     */
    private String kafkaMetricTopic = "topic-metric";

    /**
     * kafka配置参数 心跳数据发送topic名称
     */
    private String kafkaHeartbeatTopic = "topic-heartbeat";

    /**
     * kafka配置参数 producer需要server接收到数据之后发出的确认接收的信号 ack 0,1,all
     */
    private long kafkaAcks = 1L;

    /**
     * kafka配置参数 控制生产者发送请求最大大小,默认1M （这个参数和Kafka主机的message.max.bytes 参数有关系）
     */
    private long kafkaMaxRequestSize = CommonConst.KAFKA_MAX_REQUEST_SIZE;

    /**
     * kafka配置参数 生产者内存缓冲区大小 32M
     */
    private long kafkaBufferMemory = CommonConst.KAFKA_BUFFER_MEMORY;

    /**
     * kafka配置参数 重发消息次数
     */
    private long kafkaRetries = 0L;

    /**
     * kafka配置参数 客户端将等待请求的响应的最大时间
     */
    private long kafkaRequestTimeoutMs = CommonConst.KAFKA_REQUEST_TIMEOUT_MS;

    /**
     * kafka配置参数 最大阻塞时间，超过则抛出异常
     */
    private long kafkaMaxBlockMs = CommonConst.KAFKA_MAX_BLOCK_MS;

    /**
     * 配置jaas前缀
     */
    private String kafkaJaasConfig = ConfigConst.KAFKA_JAAS_CONFIG;

    /**
     * SASL鉴权机制
     */
    private String kafkaSaslMechanism = ConfigConst.KAFKA_SASL_MECHANISM;

    /**
     * 加密协议，目前支持SASL_SSL协议
     */
    private String kafkaSecurityProtocol = ConfigConst.KAFKA_SECURITY_PROTOCOL;

    /**
     * ssl truststore文件存放位置
     */
    private String kafkaSslTruststoreLocation = ConfigConst.KAFKA_SSL_TRUSTSTORE_LOCATION;

    /**
     * ssl truststore密码配置
     */
    private String kafkaSslTruststorePassword = ConfigConst.KAFKA_SSL_TRUSTSTORE_PASSWORD;

    /**
     * 域名不校验
     */
    private String kafkaIdentificationAlgorithm = ConfigConst.KAFKA_IDENTIFICATION_ALGORITHM;

    /**
     * 是否通过ssl认证
     */
    private boolean isKafkaSsl = ConfigConst.IS_KAFKA_SSL;

    /**
     * servicecomb-kie地址，当配置中心配为servicecomb-kie时需填写正确的kie服务地址
     */
    private String configKieAddress = "localhost:30110";

    /**
     * 标签监听的服务名 为空则使用IdentityConfigManager#getAppName() 否则使用配置的服务名
     */
    private String configServiceName = "sermantService";

    /**
     * 是否开启数据采集 包含心跳、指标
     */
    private boolean openMetricCollector = false;

    /**
     * 是否使用线上cse配置规则
     */
    private boolean useCseRule = false;

    /**
     * 是否适配源泛PAAS的UI以及前后端zookeeper
     */
    private boolean adaptPass = false;

    /**
     * 是否抛出业务异常 默认 false
     */
    private boolean needThrowBizException = false;

    /**
     * 指标数据发送方式 默认Netty
     */
    private MetricSendWay sendWay = MetricSendWay.NETTY;

    /**
     * 等待指标数据写入文件的等待时间 单位MS
     */
    private long metricSleepTimeMs = CommonConst.DEFAULT_METRIC_SEND_INTERVAL_MS;

    /**
     * 流控框架类型
     */
    private FlowFramework flowFramework = FlowFramework.RESILIENCE;

    /**
     * 针对apache dubbo重试异常
     */
    private String[] apacheDubboRetryExceptions = {"org.apache.dubbo.rpc.RpcException"};

    /**
     * 针对alibaba dubbo重试异常
     */
    private String[] alibabaDubboRetryExceptions = {"com.alibaba.dubbo.rpc.RpcException"};

    /**
     * 针对alibaba dubbo重试异常
     */
    private String[] springRetryExceptions = {"org.springframework.web.client.HttpServerErrorException"};

    /**
     * 是否使用agent自身配置中心 该配置主要在适配cse时，可能需要使用cse的配置中心，而非使用agent自身配置中心
     */
    private boolean useAgentConfigCenter = true;

    /**
     * kie命名空间
     */
    private String project = "default";

    /**
     * sc app配置
     */
    private String application = "sermant";

    /**
     * sc 环境配置
     */
    private String environment = "production";

    /**
     * 默认sc版本
     */
    private String version = "1.0.0";

    /**
     * 是否开启sc的加密
     */
    private boolean isSslEnabled = false;

    /**
     * 是否是基于servicecomb sdk开发 通过此确定是否需要采用拦截方式获取服务信息
     */
    private boolean isBaseSdk = false;

    /**
     * dubbo注入的cluster invoker执行器 若该配置为“close”则不适用agent的执行器
     */
    private String retryClusterInvoker = "sermant";

    /**
     * 最大缓存数
     */
    private int maxCacheSize = ConfigConst.DEFAULT_MAX_CACHE_SIZE;

    /**
     * 缓存定时器检查间隔, 单位秒
     */
    private long timedCheckInterval = ConfigConst.DEFAULT_TIME_CACHE_CHECK_INTERVAL;

    /**
     * 是否开启重试
     */
    private boolean enableRetry = true;

    /**
     * 连接超时时间-restTemplate
     */
    private long restTemplateConnectTimeoutMs = ConfigConst.CONNECT_TIMEOUT_MS;

    /**
     * 响应超时时间-restTemplate
     */
    private long restTemplateReadTimeoutMs = ConfigConst.SOCKET_READ_TIMEOUT_MS;

    /**
     * rest template-请求协议, 默认okHttp， 若宿主没有则使用Connection模式
     */
    private String restTemplateRequestFactory = "okHttp";

    public String getRestTemplateRequestFactory() {
        return restTemplateRequestFactory;
    }

    public void setRestTemplateRequestFactory(String restTemplateRequestFactory) {
        this.restTemplateRequestFactory = restTemplateRequestFactory;
    }

    public long getRestTemplateConnectTimeoutMs() {
        return restTemplateConnectTimeoutMs;
    }

    public void setRestTemplateConnectTimeoutMs(long restTemplateConnectTimeoutMs) {
        this.restTemplateConnectTimeoutMs = restTemplateConnectTimeoutMs;
    }

    public long getRestTemplateReadTimeoutMs() {
        return restTemplateReadTimeoutMs;
    }

    public void setRestTemplateReadTimeoutMs(long restTemplateReadTimeoutMs) {
        this.restTemplateReadTimeoutMs = restTemplateReadTimeoutMs;
    }

    public boolean isEnableRetry() {
        return enableRetry;
    }

    public void setEnableRetry(boolean enableRetry) {
        this.enableRetry = enableRetry;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public long getTimedCheckInterval() {
        return timedCheckInterval;
    }

    public void setTimedCheckInterval(long timedCheckInterval) {
        this.timedCheckInterval = timedCheckInterval;
    }

    public String getRetryClusterInvoker() {
        return retryClusterInvoker;
    }

    public void setRetryClusterInvoker(String retryClusterInvoker) {
        this.retryClusterInvoker = retryClusterInvoker;
    }

    public boolean isBaseSdk() {
        return isBaseSdk;
    }

    public void setBaseSdk(boolean baseSdk) {
        this.isBaseSdk = baseSdk;
    }

    public boolean isUseAgentConfigCenter() {
        return useAgentConfigCenter;
    }

    public void setUseAgentConfigCenter(boolean useAgentConfigCenter) {
        this.useAgentConfigCenter = useAgentConfigCenter;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isSslEnabled() {
        return isSslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.isSslEnabled = sslEnabled;
    }

    public String[] getApacheDubboRetryExceptions() {
        return apacheDubboRetryExceptions;
    }

    public void setApacheDubboRetryExceptions(String[] apacheDubboRetryExceptions) {
        this.apacheDubboRetryExceptions = apacheDubboRetryExceptions;
    }

    public String[] getAlibabaDubboRetryExceptions() {
        return alibabaDubboRetryExceptions;
    }

    public void setAlibabaDubboRetryExceptions(String[] alibabaDubboRetryExceptions) {
        this.alibabaDubboRetryExceptions = alibabaDubboRetryExceptions;
    }

    public String[] getSpringRetryExceptions() {
        return springRetryExceptions;
    }

    public void setSpringRetryExceptions(String[] springRetryExceptions) {
        this.springRetryExceptions = springRetryExceptions;
    }

    public FlowFramework getFlowFramework() {
        return flowFramework;
    }

    public void setFlowFramework(FlowFramework flowFramework) {
        this.flowFramework = flowFramework;
    }

    public boolean isNeedThrowBizException() {
        return needThrowBizException;
    }

    public void setNeedThrowBizException(boolean needThrowBizException) {
        this.needThrowBizException = needThrowBizException;
    }

    public long getMetricSleepTimeMs() {
        return metricSleepTimeMs;
    }

    public void setMetricSleepTimeMs(long metricSleepTimeMs) {
        this.metricSleepTimeMs = metricSleepTimeMs;
    }

    public MetricSendWay getSendWay() {
        return sendWay;
    }

    public void setSendWay(MetricSendWay sendWay) {
        this.sendWay = sendWay;
    }

    public boolean isAdaptPass() {
        return adaptPass;
    }

    public void setAdaptPass(boolean adaptPass) {
        this.adaptPass = adaptPass;
    }

    public boolean isUseCseRule() {
        return useCseRule;
    }

    public void setUseCseRule(boolean useCseRule) {
        this.useCseRule = useCseRule;
    }

    public String getConfigServiceName() {
        return configServiceName;
    }

    public void setConfigServiceName(String configServiceName) {
        this.configServiceName = configServiceName;
    }

    public boolean isOpenMetricCollector() {
        return openMetricCollector;
    }

    public void setOpenMetricCollector(boolean openMetricCollector) {
        this.openMetricCollector = openMetricCollector;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public long getMetricInterval() {
        return metricInterval;
    }

    public void setMetricInterval(long metricInterval) {
        this.metricInterval = metricInterval;
    }

    public long getMetricInitialDuration() {
        return metricInitialDuration;
    }

    public void setMetricInitialDuration(long metricInitialDuration) {
        this.metricInitialDuration = metricInitialDuration;
    }

    public long getMetricMaxLine() {
        return metricMaxLine;
    }

    public void setMetricMaxLine(long metricMaxLine) {
        this.metricMaxLine = metricMaxLine;
    }

    public long getMetricSleepTime() {
        return metricSleepTime;
    }

    public void setMetricSleepTime(long metricSleepTime) {
        this.metricSleepTime = metricSleepTime;
    }

    public String getKafkaKeySerializer() {
        return kafkaKeySerializer;
    }

    public void setKafkaKeySerializer(String kafkaKeySerializer) {
        this.kafkaKeySerializer = kafkaKeySerializer;
    }

    public String getKafkaValueSerializer() {
        return kafkaValueSerializer;
    }

    public void setKafkaValueSerializer(String kafkaValueSerializer) {
        this.kafkaValueSerializer = kafkaValueSerializer;
    }

    public String getKafkaMetricTopic() {
        return kafkaMetricTopic;
    }

    public void setKafkaMetricTopic(String kafkaMetricTopic) {
        this.kafkaMetricTopic = kafkaMetricTopic;
    }

    public String getKafkaHeartbeatTopic() {
        return kafkaHeartbeatTopic;
    }

    public void setKafkaHeartbeatTopic(String kafkaHeartbeatTopic) {
        this.kafkaHeartbeatTopic = kafkaHeartbeatTopic;
    }

    public long getKafkaAcks() {
        return kafkaAcks;
    }

    public void setKafkaAcks(long kafkaAcks) {
        this.kafkaAcks = kafkaAcks;
    }

    public long getKafkaMaxRequestSize() {
        return kafkaMaxRequestSize;
    }

    public void setKafkaMaxRequestSize(long kafkaMaxRequestSize) {
        this.kafkaMaxRequestSize = kafkaMaxRequestSize;
    }

    public long getKafkaBufferMemory() {
        return kafkaBufferMemory;
    }

    public void setKafkaBufferMemory(long kafkaBufferMemory) {
        this.kafkaBufferMemory = kafkaBufferMemory;
    }

    public long getKafkaRetries() {
        return kafkaRetries;
    }

    public void setKafkaRetries(long kafkaRetries) {
        this.kafkaRetries = kafkaRetries;
    }

    public long getKafkaRequestTimeoutMs() {
        return kafkaRequestTimeoutMs;
    }

    public void setKafkaRequestTimeoutMs(long kafkaRequestTimeoutMs) {
        this.kafkaRequestTimeoutMs = kafkaRequestTimeoutMs;
    }

    public long getKafkaMaxBlockMs() {
        return kafkaMaxBlockMs;
    }

    public void setKafkaMaxBlockMs(long kafkaMaxBlockMs) {
        this.kafkaMaxBlockMs = kafkaMaxBlockMs;
    }

    public String getKafkaJaasConfig() {
        return kafkaJaasConfig;
    }

    public void setKafkaJaasConfig(String kafkaJaasConfig) {
        this.kafkaJaasConfig = kafkaJaasConfig;
    }

    public String getKafkaSaslMechanism() {
        return kafkaSaslMechanism;
    }

    public void setKafkaSaslMechanism(String kafkaSaslMechanism) {
        this.kafkaSaslMechanism = kafkaSaslMechanism;
    }

    public String getKafkaSecurityProtocol() {
        return kafkaSecurityProtocol;
    }

    public void setKafkaSecurityProtocol(String kafkaSecurityProtocol) {
        this.kafkaSecurityProtocol = kafkaSecurityProtocol;
    }

    public String getKafkaSslTruststoreLocation() {
        return kafkaSslTruststoreLocation;
    }

    public void setKafkaSslTruststoreLocation(String kafkaSslTruststoreLocation) {
        this.kafkaSslTruststoreLocation = kafkaSslTruststoreLocation;
    }

    public String getKafkaSslTruststorePassword() {
        return kafkaSslTruststorePassword;
    }

    public void setKafkaSslTruststorePassword(String kafkaSslTruststorePassword) {
        this.kafkaSslTruststorePassword = kafkaSslTruststorePassword;
    }

    public String getKafkaIdentificationAlgorithm() {
        return kafkaIdentificationAlgorithm;
    }

    public void setKafkaIdentificationAlgorithm(String kafkaIdentificationAlgorithm) {
        this.kafkaIdentificationAlgorithm = kafkaIdentificationAlgorithm;
    }

    public boolean isKafkaSsl() {
        return isKafkaSsl;
    }

    public void setKafkaSsl(boolean kafkaSsl) {
        this.isKafkaSsl = kafkaSsl;
    }

    public String getConfigKieAddress() {
        return configKieAddress;
    }

    public void setConfigKieAddress(String configKieAddress) {
        this.configKieAddress = configKieAddress;
    }
}
