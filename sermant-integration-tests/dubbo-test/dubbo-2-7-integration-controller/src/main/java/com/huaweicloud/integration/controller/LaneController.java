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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.integration.controller;

import com.huaweicloud.integration.constants.Constant;
import com.huaweicloud.integration.entity.LaneTestEntity;
import com.huaweicloud.integration.entity.TestEntity;
import com.huaweicloud.integration.service.LaneService;
import com.huaweicloud.integration.utils.RpcContextUtils;

import org.apache.dubbo.rpc.RpcContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

/**
 * 泳道测试
 *
 * @author provenceee
 * @since 2023-03-02
 */
@RestController
@RequestMapping("/controller")
public class LaneController {
    @Resource(name = "laneService")
    private LaneService laneService;

    /**
     * 获取泳道
     *
     * @param entity entity
     * @return map
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @GetMapping("/getLaneByDubbo")
    public Map<String, Object> getLaneByDubbo(TestEntity entity) throws ExecutionException, InterruptedException {
        RpcContextUtils.setContextTagToAttachment(Constant.LANE_TEST_USER_ID, String.valueOf(entity.getId()));
        Map<String, Object> map = new HashMap<>();
        map.put("name", entity.getMapName());
        laneService.getLaneByDubbo(entity.getName(), new LaneTestEntity(entity.getLaneId(), entity.isEnabled()),
                new String[]{entity.getArrName()}, Collections.singletonList(entity.getListId()), map);
        Future<Map<String, Object>> future = RpcContext.getContext().getFuture();
        return future.get();
    }

    /**
     * 获取泳道
     *
     * @param name name
     * @param id id
     * @param enabled enabled
     * @return map
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @GetMapping("/getLaneByFeign")
    public Map<String, Object> getLaneByFeign(@RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "id", defaultValue = "0") int id,
            @RequestParam(value = "enabled", defaultValue = "false") boolean enabled)
            throws ExecutionException, InterruptedException {
        RpcContext.getContext().setAttachment(Constant.LANE_TEST_USER_ID, String.valueOf(id));
        laneService.getLaneByFeign(name, new LaneTestEntity(id, enabled));
        Future<Map<String, Object>> future = RpcContext.getContext().getFuture();
        return future.get();
    }

    /**
     * 获取泳道
     *
     * @param name name
     * @param id id
     * @param enabled enabled
     * @return map
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @GetMapping("/getLaneByRest")
    public Map<String, Object> getLaneByRest(@RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "id", defaultValue = "0") int id,
            @RequestParam(value = "enabled", defaultValue = "false") boolean enabled)
            throws ExecutionException, InterruptedException {
        RpcContext.getContext().setAttachment(Constant.LANE_TEST_USER_ID, String.valueOf(id));
        laneService.getLaneByRest(name, new LaneTestEntity(id, enabled));
        Future<Map<String, Object>> future = RpcContext.getContext().getFuture();
        return future.get();
    }
}