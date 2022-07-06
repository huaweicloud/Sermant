/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol.service;

import com.huawei.flowcontrol.common.config.FlowControlConfig;
import com.huawei.flowcontrol.common.enums.FlowFramework;
import com.huawei.flowcontrol.common.exception.InvokerWrapperException;
import com.huawei.flowcontrol.common.handler.retry.RetryContext;
import com.huawei.flowcontrol.common.support.ReflectMethodCacheSupport;
import com.huawei.flowcontrol.retry.handler.RetryHandlerV2;
import com.huawei.flowcontrol.service.rest4j.DubboRest4jService;
import com.huawei.flowcontrol.service.rest4j.HttpRest4jService;
import com.huawei.flowcontrol.service.sen.DubboSenService;
import com.huawei.flowcontrol.service.sen.HttpSenService;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.Interceptor;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.service.ServiceManager;

import io.github.resilience4j.retry.RetryConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 拦截器功能支持
 *
 * @author zhouss
 * @since 2022-01-25
 */
public abstract class InterceptorSupporter extends ReflectMethodCacheSupport implements Interceptor {
    /**
     * 标记当前请求重试中
     */
    protected static final String RETRY_KEY = "$$$$RETRY$$$";

    protected static final String RETRY_VALUE = "$$$$RETRY_VALUE$$$";

    protected static final String APACHE_DUBBO_CLUSTER_CLASS_NAME = "org.apache.dubbo.rpc.cluster.Cluster";

    protected static final String ALIBABA_DUBBO_CLUSTER_CLASS_NAME = "com.alibaba.dubbo.rpc.cluster.Cluster";

    private static final String REFUSE_REPLACE_INVOKER = "close";

    protected final FlowControlConfig flowControlConfig;

    private final ReentrantLock lock = new ReentrantLock();

    private RetryHandlerV2 retryHandler = null;

    private DubboService dubboService;

    private HttpService httpService;

    /**
     * 构造器
     */
    protected InterceptorSupporter() {
        flowControlConfig = PluginConfigManager.getPluginConfig(FlowControlConfig.class);
    }

    /**
     * 获取重试处理器
     *
     * @return RetryHandlerV2
     */
    protected final RetryHandlerV2 getRetryHandler() {
        if (retryHandler == null) {
            lock.lock();
            try {
                retryHandler = new RetryHandlerV2();
            } finally {
                lock.unlock();
            }
        }
        return retryHandler;
    }

    /**
     * 获取选择后的DUBBO服务
     *
     * @return DubboService
     */
    protected final DubboService chooseDubboService() {
        if (dubboService == null) {
            lock.lock();
            try {
                if (flowControlConfig.getFlowFramework() == FlowFramework.SENTINEL) {
                    dubboService = ServiceManager.getService(DubboSenService.class);
                } else {
                    dubboService = ServiceManager.getService(DubboRest4jService.class);
                }
            } finally {
                lock.unlock();
            }
        }
        return dubboService;
    }

    /**
     * 获取选择后的HTTP服务
     *
     * @return HttpService
     */
    protected final HttpService chooseHttpService() {
        if (httpService == null) {
            lock.lock();
            try {
                if (flowControlConfig.getFlowFramework() == FlowFramework.SENTINEL) {
                    httpService = ServiceManager.getService(HttpSenService.class);
                } else {
                    httpService = ServiceManager.getService(HttpRest4jService.class);
                }
            } finally {
                lock.unlock();
            }
        }
        return httpService;
    }

    /**
     * 创建重试方法
     *
     * @param obj          增强类
     * @param method       目标方法
     * @param allArguments 方法参数
     * @param result       默认结果
     * @return 方法
     */
    protected final Supplier<Object> createRetryFunc(Object obj, Method method, Object[] allArguments, Object result) {
        return () -> {
            method.setAccessible(true);
            try {
                return method.invoke(obj, allArguments);
            } catch (IllegalAccessException ignored) {
                // ignored
            } catch (InvocationTargetException ex) {
                throw new InvokerWrapperException(ex.getTargetException());
            }
            return result;
        };
    }

    /**
     * 进行重试前的判断，若不满足条件直接返回， 防止多调用一次宿主应用接口
     *
     * @param retry     重试执行器
     * @param result    结果
     * @param throwable 第一次执行异常信息
     * @return 是否核对通过
     */
    protected final boolean needRetry(io.github.resilience4j.retry.Retry retry, Object result, Throwable throwable) {
        final long interval = retry.getRetryConfig().getIntervalBiFunction().apply(1, null);
        final RetryConfig retryConfig = retry.getRetryConfig();
        boolean isNeedRetry = isMatchResult(result, retryConfig.getResultPredicate()) || isTargetException(throwable,
            retryConfig.getExceptionPredicate());
        if (isNeedRetry) {
            try {
                // 按照第一次等待时间等待
                Thread.sleep(interval);
            } catch (InterruptedException ignored) {
                // ignored
            }
        }
        return isNeedRetry;
    }

    private boolean isMatchResult(Object result, Predicate<Object> resultPredicate) {
        return result != null && resultPredicate.test(result);
    }

    private boolean isTargetException(Throwable throwable, Predicate<Throwable> exceptionPredicate) {
        return throwable != null && exceptionPredicate.test(throwable);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        if (canInvoke(context)) {
            return doBefore(context);
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        if (canInvoke(context)) {
            return doAfter(context);
        }
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) throws Exception {
        if (canInvoke(context)) {
            return doThrow(context);
        }
        return context;
    }

    /**
     * 是否可注入dubbo cluster 加载器
     *
     * @param className 加载器类型
     * @return 是否可注入
     */
    protected final boolean canInjectClusterInvoker(String className) {
        boolean isClusterLoader =
            APACHE_DUBBO_CLUSTER_CLASS_NAME.equals(className) || ALIBABA_DUBBO_CLUSTER_CLASS_NAME.equals(className);
        return isClusterLoader && !REFUSE_REPLACE_INVOKER.equals(flowControlConfig.getRetryClusterInvoker());
    }

    /**
     * 解析异常信息
     *
     * @param throwable 异常
     * @return msg
     */
    protected String getExMsg(Throwable throwable) {
        if (throwable instanceof InvokerWrapperException) {
            return ((InvokerWrapperException)throwable).getRealException().getMessage();
        }
        return throwable.getMessage();
    }

    /**
     * 前置触发点
     *
     * @param context 执行上下文
     * @return 执行上下文
     * @throws Exception 执行异常
     */
    protected abstract ExecuteContext doBefore(ExecuteContext context) throws Exception;

    /**
     * 后置触发点
     *
     * @param context 执行上下文
     * @return 执行上下文
     * @throws Exception 执行异常
     */
    protected abstract ExecuteContext doAfter(ExecuteContext context) throws Exception;

    /**
     * 异常触发点
     *
     * @param context 执行上下文
     * @return 执行上下文
     */
    protected ExecuteContext doThrow(ExecuteContext context) {
        return context;
    }

    /**
     * 是否可调用内部方法逻辑
     *
     * @param context 上下文
     * @return 是否可调用
     */
    protected boolean canInvoke(ExecuteContext context) {
        return !RetryContext.INSTANCE.isMarkedRetry();
    }
}
