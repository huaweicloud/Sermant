/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.core.service.httpserver.api;

/**
 * 响应结果类
 *
 * @param <T> 数据类型
 * @author zwmagic
 * @since 2024-02-03
 */
public class ResponseResult<T> {

    private boolean success;

    private String message;

    private T data;

    public ResponseResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> ofSuccess() {
        return new ResponseResult<>(true, null, null);
    }

    public static <T> ResponseResult<T> ofSuccess(T data) {
        return new ResponseResult<>(true, null, data);
    }

    public static <T> ResponseResult<T> ofFailure() {
        return new ResponseResult<>(false, null, null);
    }

    public static <T> ResponseResult<T> ofFailure(T data) {
        return new ResponseResult<>(false, null, data);
    }

    public static <T> ResponseResult<T> ofFailure(Throwable t) {
        return new ResponseResult<>(false, t.getMessage(), null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
