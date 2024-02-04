/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR C¬ONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huaweicloud.sermant.implement.service.httpserver.simple;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRequest;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpResponse;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRouteHandler;
import com.huaweicloud.sermant.core.service.httpserver.config.HttpServerConfig;
import com.huaweicloud.sermant.core.service.httpserver.exception.HttpServerException;
import com.huaweicloud.sermant.implement.service.httpserver.HttpServerProvider;
import com.huaweicloud.sermant.implement.service.httpserver.common.Constants;
import com.huaweicloud.sermant.implement.service.httpserver.common.HttpRouteHandlerManager;
import com.sun.net.httpserver.HttpServer;
import org.kohsuke.MetaInfServices;

import java.net.InetSocketAddress;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author zwmagic
 * @since 2024-02-02
 */
@MetaInfServices(HttpServerProvider.class)
public class SimpleHttpServerProvider implements HttpServerProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    private HttpServer httpServer;

    @Override
    public String getType() {
        return Constants.SIMPLE_HTTP_SERVER_TYPE;
    }

    @Override
    public void start() throws Exception {
        HttpServerConfig config = ConfigManager.getConfig(HttpServerConfig.class);
        this.httpServer = HttpServer.create(new InetSocketAddress(config.getPort()), 0);

        int threads = Runtime.getRuntime().availableProcessors();
        int coreThread = config.getServerCorePoolSize() == null ? threads : config.getServerCorePoolSize();
        int maxThread = config.getServerMaxPoolSize() == null ? threads : config.getServerMaxPoolSize();
        this.httpServer.setExecutor(new ThreadPoolExecutor(coreThread, maxThread, 60000, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ThreadFactory() {
                    private final AtomicInteger threadCount = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "simpleHttpserver-" + threadCount.incrementAndGet());
                        t.setPriority(Thread.NORM_PRIORITY);
                        return t;
                    }
                }));

        httpServer.createContext("/", exchange -> {
            HttpRequest request = new SimpleHttpRequest(exchange);
            HttpResponse response = new SimpleHttpResponse(exchange);
            try {
                HttpRouteHandler handler = HttpRouteHandlerManager.getHandler(request);
                if (handler == null) {
                    throw new HttpServerException(404, "Not Found");
                }
                handler.handle(request, response);
            } catch (HttpServerException e) {
                response.status(e.getStatus());
                if (e.getStatus() < 500) {
                    response.writeBody(e.getMessage());
                } else {
                    response.writeBody(e);
                }
            } catch (Throwable e) {
                response.status(500);
                response.writeBody(e);
            }

        });
        httpServer.start();
    }


    @Override
    public void stop() throws Exception {
        if (httpServer == null) {
            return;
        }
        httpServer.stop(1);
    }

}
