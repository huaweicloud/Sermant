/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on org/apache/servicecomb/config/kie/client/KieConfigManager.java
 * from the Apache ServiceComb Java Chassis project.
 */

package com.huaweicloud.sermant.core.service.dynamicconfig.kie.listener;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.ClientUrlManager;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.KieClient;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.KieConfigEntity;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.KieListenerWrapper;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.KieRequest;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.KieResponse;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.KieSubscriber;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.client.kie.ResultHandler;
import com.huaweicloud.sermant.core.service.dynamicconfig.kie.constants.KieConstants;
import com.huaweicloud.sermant.core.service.dynamicconfig.utils.LabelGroupUtils;
import com.huaweicloud.sermant.core.utils.ThreadFactoryUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * ???????????????
 *
 * @author zhouss
 * @since 2021-11-17
 */
public class SubscriberManager {
    /**
     * ???????????????
     */
    public static final int MAX_THREAD_SIZE = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * ?????????
     */
    private static final int THREAD_SIZE = 5;

    /**
     * ??????????????????
     */
    private static final int SECONDS_UNIT = 1000;

    /**
     * ??????????????????
     */
    private static final int SCHEDULE_REQUEST_INTERVAL_MS = 5000;

    /**
     * ????????????
     */
    private static final String WAIT = "20";

    /**
     * ?????????????????????
     */
    private static final long LONG_CONNECTION_REQUEST_INTERVAL_MS = 2000L;

    /**
     * ???????????????????????? ????????????????????????????????? MAX_THREAD_SIZE
     */
    private final AtomicInteger curLongConnectionRequestCount = new AtomicInteger(0);

    /**
     * map< ?????????, ?????????????????????????????? >  ??????group???????????????KieListenerWrapper
     */
    private final Map<KieRequest, KieListenerWrapper> listenerMap =
        new ConcurrentHashMap<>();

    /**
     * kie?????????
     */
    private final KieClient kieClient;

    /**
     * ??????????????????????????????disabled?????????
     */
    private final ResultHandler<KieResponse> receiveAllDataHandler = new ResultHandler.DefaultResultHandler(false);

    /**
     * ??????????????? ????????????MAX_THREAD_SIZE????????? ????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private final ThreadPoolExecutor longRequestExecutor = new ThreadPoolExecutor(THREAD_SIZE, MAX_THREAD_SIZE, 0,
        TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadFactoryUtils("kie-subscribe-long-task"));

    /**
     * ?????????????????????
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * ????????????
     *
     * @param serverAddress serverAddress
     */
    public SubscriberManager(String serverAddress) {
        kieClient = new KieClient(new ClientUrlManager(serverAddress));
    }

    /**
     * SubscriberManager
     *
     * @param serverAddress serverAddress
     * @param project       project
     */
    public SubscriberManager(String serverAddress, String project) {
        kieClient = new KieClient(new ClientUrlManager(serverAddress), project);
    }

    /**
     * ???????????????
     *
     * @param group    ?????????
     * @param listener ?????????
     * @param ifNotify ?????????????????????????????????????????????????????????????????????
     * @return ??????????????????
     */
    public boolean addGroupListener(String group, DynamicConfigListener listener, boolean ifNotify) {
        try {
            return subscribe(KieConstants.DEFAULT_GROUP_KEY,
                new KieRequest().setLabelCondition(LabelGroupUtils.getLabelCondition(group)).setWait(WAIT), listener,
                ifNotify);
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Add group listener failed, %s", ex.getMessage()));
            return false;
        }
    }

    /**
     * ????????????key?????????
     *
     * @param key      ???
     * @param group    ?????????
     * @param listener ?????????
     * @param ifNotify ?????????????????????????????????????????????????????????????????????
     * @return ??????????????????
     */
    public boolean addConfigListener(String key, String group, DynamicConfigListener listener, boolean ifNotify) {
        try {
            return subscribe(key,
                new KieRequest().setLabelCondition(LabelGroupUtils.getLabelCondition(group)).setWait(WAIT), listener,
                ifNotify);
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Add group listener failed, %s", ex.getMessage()));
            return false;
        }
    }

    /**
     * ???????????????
     *
     * @param group    ?????????
     * @param listener ?????????
     * @return ??????????????????
     */
    public boolean removeGroupListener(String group, DynamicConfigListener listener) {
        try {
            return unSubscribe(KieConstants.DEFAULT_GROUP_KEY,
                new KieRequest().setLabelCondition(LabelGroupUtils.getLabelCondition(group)).setWait(WAIT), listener);
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Removed group listener failed, %s", ex.getMessage()));
            return false;
        }
    }

    /**
     * ????????????, ???????????????????????????????????????
     *
     * @param key     ?????????
     * @param group   ??????
     * @param content ????????????
     * @return ??????????????????
     */
    public boolean publishConfig(String key, String group, String content) {
        final Optional<String> keyIdOptional = getKeyId(key, group);
        if (!keyIdOptional.isPresent()) {
            // ???????????? ??????????????????
            final Map<String, String> labels = LabelGroupUtils.resolveGroupLabels(group);
            return kieClient.publishConfig(key, labels, content, true);
        } else {
            return kieClient.doUpdateConfig(keyIdOptional.get(), content, true);
        }
    }

    /**
     * ????????????
     *
     * @param key   ?????????
     * @param group ??????
     * @return ??????????????????
     */
    public boolean removeConfig(String key, String group) {
        final Optional<String> keyIdOptional = getKeyId(key, group);
        return keyIdOptional.filter(kieClient::doDeleteConfig).isPresent();
    }

    /**
     * ??????key_id
     *
     * @param key   ???
     * @param group ???
     * @return key_id, ?????????????????????null
     */
    private Optional<String> getKeyId(String key, String group) {
        final KieResponse kieResponse = queryConfigurations(null, LabelGroupUtils.getLabelCondition(group), false);
        if (kieResponse == null || kieResponse.getData() == null) {
            return Optional.empty();
        }
        final Map<String, String> labels = LabelGroupUtils.resolveGroupLabels(group);
        for (KieConfigEntity entity : kieResponse.getData()) {
            if (isSameKey(entity, key, labels)) {
                return Optional.of(entity.getId());
            }
        }
        return Optional.empty();
    }

    private boolean isSameKey(KieConfigEntity entity, String targetKey, Map<String, String> targetLabels) {
        if (!StringUtils.equals(entity.getKey(), targetKey)) {
            return false;
        }

        // ????????????????????????
        final Map<String, String> sourceLabels = entity.getLabels();
        if (sourceLabels == null || (sourceLabels.size() != targetLabels.size())) {
            return false;
        }
        for (Map.Entry<String, String> entry : sourceLabels.entrySet()) {
            final String labelValue = targetLabels.get(entry.getKey());
            if (!StringUtils.equals(labelValue, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * ???????????????
     *
     * @param key                   key
     * @param kieRequest            ??????
     * @param dynamicConfigListener ?????????
     * @param ifNotify              ?????????????????????????????????????????????????????????????????????
     * @return boolean
     */
    public boolean subscribe(String key, KieRequest kieRequest, DynamicConfigListener dynamicConfigListener,
        boolean ifNotify) {
        final KieListenerWrapper oldWrapper = listenerMap.get(kieRequest);
        if (oldWrapper == null) {
            return firstSubscribeForGroup(key, kieRequest, dynamicConfigListener, ifNotify);
        } else {
            oldWrapper.addKeyListener(key, dynamicConfigListener);
            tryNotify(oldWrapper.getKieRequest(), oldWrapper, ifNotify);
            return true;
        }
    }

    private void tryNotify(KieRequest request, KieListenerWrapper wrapper, boolean ifNotify) {
        if (ifNotify) {
            firstRequest(request, wrapper);
        }
    }

    private boolean firstSubscribeForGroup(String key, KieRequest kieRequest,
        DynamicConfigListener dynamicConfigListener, boolean ifNotify) {
        final KieSubscriber kieSubscriber = new KieSubscriber(kieRequest);
        Task task;
        KieListenerWrapper kieListenerWrapper =
            new KieListenerWrapper(key, dynamicConfigListener, new KvDataHolder(), kieRequest);
        if (!kieSubscriber.isLongConnectionRequest()) {
            task = new ShortTimerTask(kieSubscriber, kieListenerWrapper);
        } else {
            if (exceedMaxLongRequestCount()) {
                LOGGER.warning(String.format(Locale.ENGLISH,
                    "Exceeded max long connection request subscribers, the max number is %s, it will be discarded!",
                    curLongConnectionRequestCount.get()));
                return false;
            }
            buildRequestConfig(kieRequest);
            task = new LoopPullTask(kieSubscriber, kieListenerWrapper);
        }
        kieListenerWrapper.setTask(task);
        listenerMap.put(kieRequest, kieListenerWrapper);
        tryNotify(kieRequest, kieListenerWrapper, ifNotify);
        executeTask(task);
        return true;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return boolean
     */
    private boolean exceedMaxLongRequestCount() {
        return curLongConnectionRequestCount.incrementAndGet() > MAX_THREAD_SIZE;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param kieRequest         ?????????
     * @param kieListenerWrapper ?????????
     */
    public void firstRequest(KieRequest kieRequest, KieListenerWrapper kieListenerWrapper) {
        try {
            KieResponse kieResponse = queryConfigurations(null, kieRequest.getLabelCondition());
            if (kieResponse != null && kieResponse.isChanged()) {
                tryPublishEvent(kieResponse, kieListenerWrapper, true);
                kieRequest.setRevision(kieResponse.getRevision());
            }
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Pull the first request failed! %s", ex.getMessage()));
        }
    }

    /**
     * ??????????????????
     *
     * @param revision ??????
     * @param label    ???????????????
     * @return kv??????
     */
    public KieResponse queryConfigurations(String revision, String label) {
        return queryConfigurations(revision, label, true);
    }

    /**
     * ??????????????????
     *
     * @param revision    ??????
     * @param label       ???????????????
     * @param onlyEnabled ???????????????status=enabled
     * @return kv??????
     */
    public KieResponse queryConfigurations(String revision, String label, boolean onlyEnabled) {
        final KieRequest cloneRequest = new KieRequest().setRevision(revision).setLabelCondition(label);
        if (onlyEnabled) {
            return kieClient.queryConfigurations(cloneRequest);
        }
        return kieClient.queryConfigurations(cloneRequest, receiveAllDataHandler);
    }

    /**
     * ????????????
     *
     * @param key                   key
     * @param kieRequest            kieRequest
     * @param dynamicConfigListener dynamicConfigListener
     * @return boolean
     */
    public boolean unSubscribe(String key, KieRequest kieRequest, DynamicConfigListener dynamicConfigListener) {
        for (Map.Entry<KieRequest, KieListenerWrapper> next : listenerMap.entrySet()) {
            if (!next.getKey().equals(kieRequest)) {
                continue;
            }
            if (dynamicConfigListener == null) {
                listenerMap.remove(next.getKey());
                return true;
            } else {
                final KieListenerWrapper wrapper = next.getValue();
                if (wrapper.removeKeyListener(key, dynamicConfigListener)) {
                    if (wrapper.isEmpty()) {
                        // ?????????????????????????????????????????????????????????
                        wrapper.getTask().stop();
                    }
                    return true;
                }
            }
        }
        LOGGER.warning(
            String.format(Locale.ENGLISH, "The subscriber of group %s not found!", kieRequest.getLabelCondition()));
        return false;
    }

    private void buildRequestConfig(KieRequest kieRequest) {
        int wait = (Integer.parseInt(kieRequest.getWait()) + 1) * SECONDS_UNIT;
        if (kieRequest.getRequestConfig() == null) {
            kieRequest.setRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(wait).setConnectTimeout(wait)
                .setSocketTimeout(wait).build());
        }
    }

    private void executeTask(final Task task) {
        try {
            if (task.isLongConnectionRequest()) {
                longRequestExecutor.execute(new TaskRunnable(task));
            } else {
                if (scheduledExecutorService == null) {
                    synchronized (SubscriberManager.class) {
                        if (scheduledExecutorService == null) {
                            scheduledExecutorService = new ScheduledThreadPoolExecutor(THREAD_SIZE,
                                new ThreadFactoryUtils("kie-subscribe-task"));
                        }
                    }
                }
                scheduledExecutorService.scheduleAtFixedRate(new TaskRunnable(task), 0, SCHEDULE_REQUEST_INTERVAL_MS,
                    TimeUnit.MILLISECONDS);
            }
        } catch (RejectedExecutionException ex) {
            LOGGER.warning("Rejected the task " + task.getClass() + " " + ex.getMessage());
        }
    }

    private void tryPublishEvent(KieResponse kieResponse, KieListenerWrapper kieListenerWrapper, boolean isFirst) {
        final KvDataHolder kvDataHolder = kieListenerWrapper.getKvDataHolder();
        final KvDataHolder.EventDataHolder eventDataHolder = kvDataHolder.analyzeLatestData(kieResponse, isFirst);
        if (eventDataHolder.isChanged() || isFirst) {
            kieListenerWrapper.notifyListeners(eventDataHolder, isFirst);
        }
    }

    /**
     * TaskRunnable
     *
     * @since 2021-11-17
     */
    static class TaskRunnable implements Runnable {
        private final Task task;

        TaskRunnable(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.execute();
            } catch (Exception ex) {
                LOGGER.warning(
                    String.format(Locale.ENGLISH, "The error occurred when execute task , %s", ex.getMessage()));
            }
        }
    }

    /**
     * Task
     *
     * @since 2021-11-17
     */
    public interface Task {
        /**
         * ????????????
         */
        void execute();

        /**
         * ???????????????????????????????????????
         *
         * @return boolean
         */
        boolean isLongConnectionRequest();

        /**
         * ????????????
         */
        void stop();
    }

    /**
     * AbstractTask
     *
     * @since 2021-11-17
     */
    abstract static class AbstractTask implements Task {
        protected volatile boolean isContinue = true;

        @Override
        public void execute() {
            if (!isContinue) {
                return;
            }
            executeInner();
        }

        @Override
        public void stop() {
            isContinue = false;
        }

        /**
         * ??????????????????
         */
        public abstract void executeInner();
    }

    /**
     * ??????????????????
     *
     * @since 2021-11-17
     */
    class ShortTimerTask extends AbstractTask {
        private final KieSubscriber kieSubscriber;

        private final KieListenerWrapper kieListenerWrapper;

        ShortTimerTask(KieSubscriber kieSubscriber, KieListenerWrapper kieListenerWrapper) {
            this.kieSubscriber = kieSubscriber;
            this.kieListenerWrapper = kieListenerWrapper;
        }

        @Override
        public void executeInner() {
            final KieResponse kieResponse = kieClient.queryConfigurations(kieSubscriber.getKieRequest());
            if (kieResponse != null && kieResponse.isChanged()) {
                tryPublishEvent(kieResponse, kieListenerWrapper, false);
                kieSubscriber.getKieRequest().setRevision(kieResponse.getRevision());
            }
        }

        @Override
        public boolean isLongConnectionRequest() {
            return false;
        }
    }

    /**
     * LoopPullTask
     *
     * @since 2021-11-17
     */
    class LoopPullTask extends AbstractTask {
        private final KieSubscriber kieSubscriber;

        private final KieListenerWrapper kieListenerWrapper;

        private int failCount;

        LoopPullTask(KieSubscriber kieSubscriber, KieListenerWrapper kieListenerWrapper) {
            this.kieSubscriber = kieSubscriber;
            this.kieListenerWrapper = kieListenerWrapper;
        }

        @Override
        public void executeInner() {
            try {
                final KieResponse kieResponse = kieClient.queryConfigurations(kieSubscriber.getKieRequest());
                if (kieResponse != null && kieResponse.isChanged()) {
                    tryPublishEvent(kieResponse, kieListenerWrapper, false);
                    kieSubscriber.getKieRequest().setRevision(kieResponse.getRevision());
                }

                // ?????????????????????????????????????????????;?????????????????????????????????????????????????????????????????????revision???????????????????????????????????????????????????????????????????????????????????????
                this.failCount = 0;
                SubscriberManager.this.executeTask(new SleepCallBackTask(this, LONG_CONNECTION_REQUEST_INTERVAL_MS));
            } catch (Exception ex) {
                LOGGER.warning(
                    String.format(Locale.ENGLISH, "pull kie config failed, %s, it will rePull", ex.getMessage()));
                ++failCount;
                SubscriberManager.this.executeTask(new SleepCallBackTask(this, failCount));
            }
        }

        @Override
        public boolean isLongConnectionRequest() {
            return kieSubscriber.isLongConnectionRequest();
        }
    }

    /**
     * SleepCallBackTask
     *
     * @since 2021-11-17
     */
    class SleepCallBackTask extends AbstractTask {
        private static final long MAX_WAIT_MS = 60 * 1000 * 60L;

        private static final long BASE_MS = 3000L;

        private final Task nextTask;

        private int failedCount;

        private long waitTimeMs;

        SleepCallBackTask(Task nextTask, int failedCount) {
            this.nextTask = nextTask;
            this.failedCount = failedCount;
        }

        SleepCallBackTask(Task nextTask, long waitTimeMs) {
            this.nextTask = nextTask;
            this.waitTimeMs = waitTimeMs;
        }

        @Override
        public void executeInner() {
            long wait;
            if (waitTimeMs != 0) {
                wait = Math.min(waitTimeMs, MAX_WAIT_MS);
            } else {
                wait = Math.min(MAX_WAIT_MS, BASE_MS * failedCount * failedCount);
            }
            try {
                Thread.sleep(wait);
                SubscriberManager.this.executeTask(nextTask);
            } catch (InterruptedException ignored) {
                // ignored
            }
        }

        @Override
        public boolean isLongConnectionRequest() {
            return nextTask.isLongConnectionRequest();
        }

        @Override
        public void stop() {
            nextTask.stop();
        }
    }
}
