/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.sermant.backend.server;

import com.huawei.sermant.backend.cache.DeleteTimeoutData;
import com.huawei.sermant.backend.common.conf.DataTypeTopicMapping;
import com.huawei.sermant.backend.kafka.KafkaConsumerManager;
import com.huawei.sermant.backend.pojo.Message;
import com.huawei.sermant.backend.common.conf.KafkaConf;
import com.huawei.sermant.backend.common.exception.KafkaTopicException;
import com.huawei.sermant.backend.kafka.KafkaProducerManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Timer;
import javax.annotation.PostConstruct;

/**
 * ???????????????
 *
 * @author lilai
 * @version 0.0.1
 * @since 2021-08-07
 */
@Component
public class NettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    // ???????????????????????????
    private static final int CONNECTION_SIZE = 1024;

    // ????????????????????????
    private static final int DELETE_TIMEOUT_DATA_DELAY_TIME = 5000;

    // ??????????????????????????????
    private static final int DELETE_TIMEOUT_DATA_PERIOD_TIME = 3000;

    private static final Timer TIMER = new Timer();

    // ???????????????
    @Value("${netty.wait.time}")
    private int readWaitTime = 60;

    // ????????????
    @Value("${netty.port}")
    private int port;

    // kafka??????????????????
    @Autowired
    private KafkaConf conf;

    @Autowired
    private DataTypeTopicMapping topicMapping;

    /**
     * ?????????????????????
     *
     * ???tomcat????????????????????????????????????????????????
     */
    @PostConstruct
    public void start() {
        LOGGER.info("Starting the netty server...");

        // ??????????????????
        TIMER.schedule(new DeleteTimeoutData(), DELETE_TIMEOUT_DATA_DELAY_TIME, DELETE_TIMEOUT_DATA_PERIOD_TIME);

        // ????????????????????????
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        // ????????????????????????
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            KafkaProducer<String, byte[]> producer = KafkaProducerManager.getInstance(conf).getProducer();
            KafkaConsumer<String, String> consumer = KafkaConsumerManager.getInstance(conf).getConsumer();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, CONNECTION_SIZE)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            ChannelPipeline pipeline = channel.pipeline();

                            // ????????????????????????????????????????????????????????????????????????????????????
                            pipeline.addLast(new IdleStateHandler(readWaitTime, 0, 0));
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(Message.NettyMessage.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ServerHandler(producer, consumer, topicMapping,
                                    conf.getIsHeartbeatCache()));
                        }
                    });

            // ??????????????????????????????
            serverBootstrap.bind(port).sync();
            LOGGER.info("Netty server start, port is {}", port);
        } catch (InterruptedException | KafkaTopicException e) {
            LOGGER.error("Exception occurs when start netty server, exception message : {}", e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
