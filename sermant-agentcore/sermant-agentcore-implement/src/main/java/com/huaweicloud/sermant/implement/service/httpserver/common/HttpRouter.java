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
package com.huaweicloud.sermant.implement.service.httpserver.common;

import com.huaweicloud.sermant.core.service.httpserver.annotation.HttpRouteMapping;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpMethod;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRequest;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRouteHandler;
import com.huaweicloud.sermant.implement.service.httpserver.exception.HttpMethodNotAllowedException;

import java.util.regex.Pattern;

/**
 * @author zwmagic
 * @since 2024-02-03
 */
public class HttpRouter {
    private final Pattern pattern;

    private final String path;
    private final HttpMethod method;
    private final HttpRouteHandler handler;

    public HttpRouter(String pluginName, HttpRouteHandler handler) {
        HttpRouteMapping annotation = handler.getClass().getAnnotation(HttpRouteMapping.class);
        this.path = buildPath(pluginName, annotation.path());
        this.pattern = Pattern.compile(exprCompile(this.path), Pattern.CASE_INSENSITIVE);
        this.method = annotation.method();
        this.handler = handler;
    }

    private String buildPath(String pluginName, String path) {
        StringBuilder builder = new StringBuilder("/").append(pluginName);
        if (path.startsWith("/")) {
            builder.append(path);
        } else {
            builder.append("/").append(path);
        }
        return builder.toString();
    }

    public boolean match(HttpRequest request) {
        if (!matchPath(request.path())) {
            return false;
        }
        if (HttpMethod.ALL.name().equals(method.name())) {
            return true;
        }
        if (method.name().equals(request.method())) {
            return true;
        }
        throw new HttpMethodNotAllowedException("Method Not Allowed");
    }

    private boolean matchPath(String uri) {
        if ("**".equals(path) || "/**".equals(path)) {
            return true;
        }
        if (path.equals(uri)) {
            return true;
        }
        return pattern.matcher(uri).find();
    }

    public HttpRouteHandler getHandler() {
        return handler;
    }

    private static String exprCompile(String expr) {
        //替换特殊符号
        String p = expr;
        p = p.replace(".", "\\.");
        p = p.replace("$", "\\$");
        //替换中间的**值
        p = p.replace("**", ".[]");
        //替换*值
        p = p.replace("*", "[^/]*");
        //替换{x}值
        if (p.contains("{")) {
            if (p.indexOf("_}") > 0) {
                p = p.replaceAll("\\{[^\\}]+?\\_\\}", "(.+?)");
            }
            p = p.replaceAll("\\{[^\\}]+?\\}", "([^/]+?)");//不采用group name,可解决_的问题
        }
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        p = p.replace(".[]", ".*");
        return "^" + p + "$";
    }

}
