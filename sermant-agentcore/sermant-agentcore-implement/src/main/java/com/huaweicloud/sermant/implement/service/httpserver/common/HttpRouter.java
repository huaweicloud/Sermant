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

package com.huaweicloud.sermant.implement.service.httpserver.common;

import com.huaweicloud.sermant.core.service.httpserver.annotation.HttpRouteMapping;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpMethod;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRequest;
import com.huaweicloud.sermant.core.service.httpserver.api.HttpRouteHandler;
import com.huaweicloud.sermant.implement.service.httpserver.exception.HttpMethodNotAllowedException;

import java.util.regex.Pattern;

/**
 * HTTP 路由
 *
 * @author zwmagic
 * @since 2024-02-03
 */
public class HttpRouter {

    private final Pattern pattern;

    private final String path;

    private final HttpMethod method;

    private final HttpRouteHandler handler;

    /**
     * 构造函数，用于创建HttpRouter对象
     *
     * @param pluginName 插件名称
     * @param handler HttpRouteHandler对象
     */
    public HttpRouter(String pluginName, HttpRouteHandler handler) {
        HttpRouteMapping annotation = handler.getClass().getAnnotation(HttpRouteMapping.class);
        this.path = buildPath(pluginName, annotation.path());
        this.pattern = Pattern.compile(exprCompile(this.path), Pattern.CASE_INSENSITIVE);
        this.method = annotation.method();
        this.handler = handler;
    }

    private String buildPath(String pluginName, String httpPath) {
        StringBuilder builder = new StringBuilder(Constants.HTTP_PATH_DIVIDER).append(pluginName);
        if (httpPath.startsWith(Constants.HTTP_PATH_DIVIDER)) {
            builder.append(httpPath);
        } else {
            builder.append(Constants.HTTP_PATH_DIVIDER).append(httpPath);
        }
        return builder.toString();
    }

    /**
     * 判断请求是否匹配当前HttpRouter
     *
     * @param request HttpRequest对象
     * @return 如果请求匹配则返回true，否则返回false
     */
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
        String expression = expr;
        expression = expression.replace(".", "\\.");
        expression = expression.replace("$", "\\$");
        expression = expression.replace("**", ".[]");
        expression = expression.replace("*", "[^/]*");
        if (expression.contains("{")) {
            if (expression.indexOf("_}") > 0) {
                expression = expression.replaceAll("\\{[^\\}]+?\\_\\}", "(.+?)");
            }
            expression = expression.replaceAll("\\{[^\\}]+?\\}", "([^/]+?)");
        }
        if (!expression.startsWith("/")) {
            expression = "/" + expression;
        }
        expression = expression.replace(".[]", ".*");
        return "^" + expression + "$";
    }

}
